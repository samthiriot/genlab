package genlab.core.exec;

import genlab.core.commons.ProgramException;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.exec.IComputationProgressSimpleListener;
import genlab.core.model.exec.IComputationResult;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Takes many tasks, and executes them.
 * 
 * 
 * 
 * Some features:
 * <ul>
 * <li>monitors the tasks queued but not ready, ready, running, done</li>
 * <li>when a task is ready, makes it available to the worker threads</li>
 * <li>ensure the worker thread pool is big enough to process the tasks</li>
 * <li>attempts to clean finished tasks to save resources</li>
 * <li>cancels and kills the threads once done</li>
 * </ul>
 * 
 * TODO use the map of threads to monitor and kill
 * TODO timeouts
 * 
 * TODO the number of threads should be only one specific type of shared resources. Other 
 * could be the network, a disk, etc.
 * 
 * @author Samuel Thiriot
 *
 */
public class Runner extends Thread implements IRunner {
	
	final static int MAX_THREADS = 12; // TODO
	
	final static int START_TASKS_SIZE = 500;
	
	
	/**
	 * Acts as the locker for: all, roots, done, ready, running, notReady
	 */
	final Set<IAlgoExecution> all = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	
	final Set<ICleanableTask> cleanable = new HashSet<ICleanableTask>(START_TASKS_SIZE);


	final Set<IAlgoExecution> roots = new HashSet<IAlgoExecution>();;

	final Set<IAlgoExecution> done = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final Set<IAlgoExecution> ready = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final Set<IAlgoExecution> running = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final Set<IAlgoExecution> notReady = new HashSet<IAlgoExecution>(START_TASKS_SIZE);	
	

	final Set<ITasksDynamicProducer> tasksProducers = new HashSet<ITasksDynamicProducer>();
	
	

		
	/**
	 * If true, all tasks should be canceled as soon as possible.
	 */
	boolean cancel = false;
	
	private ListOfMessages messagesRun = null;
	
	private final Object lockerMainLoop = new Object();
	
	/**
	 * Contains worker threads which do not use the CPU (in fact, they are more control threads.
	 * This thread pool will be resized on demand.
	 */
	private Set<WorkingRunnerThread> threadsPoolTaskNoThread = new HashSet<WorkingRunnerThread>(MAX_THREADS*20);
	final BlockingQueue<IAlgoExecution> readyToComputeNoThread = new LinkedBlockingQueue<IAlgoExecution>();
	private int usedThreadsWithoutThread = 0;

	/**
	 * Contains worker threads which do use the CPU. Contains only MAX_THREADS
	 */
	private Set<WorkingRunnerThread> threadsPoolTaskWithThreads = new HashSet<WorkingRunnerThread>(MAX_THREADS*20);
	final BlockingQueue<IAlgoExecution> readyToComputeWithThreads = new LinkedBlockingQueue<IAlgoExecution>();

	/**
	 * The number of threads used currently. Based on the number of thread displayed by tasks, 
	 * not an objective measure.
	 */
	private int usedThreads = 0;

	
	
	public Runner() {
		
		this.messagesRun =  ListsOfMessages.getGenlabMessages();
		
		setName("glRunner");
		setPriority(NORM_PRIORITY);
		setDaemon(true);
		

		// init the thread pool
		for (int i=0; i<MAX_THREADS; i++) {
			WorkingRunnerThread t = new WorkingRunnerThread(
					"gl_worker_"+i, 
					readyToComputeWithThreads
					);
			t.start();
			threadsPoolTaskWithThreads.add(t);
		}
		
		// init the thread pool
		for (int i=0; i<MAX_THREADS; i++) {
			WorkingRunnerThread t = new WorkingRunnerThread(
					"gl_workcontroller_"+i, 
					readyToComputeNoThread
					);
			t.start();
			threadsPoolTaskNoThread.add(t);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#addTasks(java.util.Collection)
	 */
	@Override
	public void addTasks(Collection<IAlgoExecution> allTasks) {
		for (IAlgoExecution e: allTasks)
			addTask(e);	
		
	}

	/**
	 * Only called when ready.
	 * 
	 * @param exec
	 */
	protected void submitTaskToWorkerThreads(IAlgoExecution exec) {
		
		if (exec.getThreadsUsed() == 0) {
			synchronized (threadsPoolTaskNoThread) {
				if (usedThreadsWithoutThread == threadsPoolTaskNoThread.size()) {
					messagesRun.debugTech("not enough threads; increasing the size to "+(threadsPoolTaskNoThread.size()+1), getClass());
					threadsPoolTaskNoThread.add(
							new WorkingRunnerThread(
									"gl_workcontroller_"+threadsPoolTaskNoThread.size(), 
									readyToComputeNoThread
									)
							);
				}
				readyToComputeNoThread.add(exec);

			}
		} else {
			readyToComputeWithThreads.add(exec);
			
		}
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#addTask(genlab.core.model.exec.IAlgoExecution)
	 */
	@Override
	public void addTask(IAlgoExecution exec) {
		
		synchronized (all) {
			
			if (all.contains(exec))
				return;
			
			all.add(exec);
			
			exec.getProgress().addListener(this);

		
			// add the task in the place it can be used
			switch (exec.getProgress().getComputationState()) {
			case READY:
				ready.add(exec);
				submitTaskToWorkerThreads(exec);
				break;
			case CREATED:
			case WAITING_DEPENDENCY:
				notReady.add(exec);
				break;
			case FINISHED_CANCEL:
			case FINISHED_FAILURE:
			case FINISHED_OK:
				// adding a task at this state is... weird.
				done.add(exec);
				break;
			case STARTED:
				// weird too
				running.add(exec);
				break;
			default: 
				throw new ProgramException("state of this task unknown: "+exec.getProgress().getComputationState());
			}
		}
		
		// wake up the thread an make him think about the idea of working.
		wakeUp();
		
		//ExecutionHooks.singleton.notifyParentTaskAdded(exec);
		
	}
	
	protected void wakeUp() {
		
		messagesRun.traceTech("attempting to wake up the runner: \"Knock knock, Neo !\"", getClass());

		synchronized (lockerMainLoop) {
			lockerMainLoop.notifyAll();
		}
		
	}

	protected Collection<IAlgoExecution> detectRoots() {
		
		LinkedList<IAlgoExecution> res = new LinkedList<IAlgoExecution>();
		
		synchronized (all) {
			for (IAlgoExecution e: all) {
				if (e.getPrerequires().isEmpty() && e.getAlgoInstance().getContainer()==null)
					res.add(e);
			}				
		}
		
		messagesRun.traceTech("found root tasks: "+res, getClass());
		
		return res;
		
	}
	
	protected void registerListeners() {
		synchronized (all) {
			for (IAlgoExecution e: all) {
				e.getProgress().addListener(this);
			}	
		}
	}
	
	/**
	 * Called at the begining of the start method
	 */
	protected void initRun() {

		// prepare our task
		/*
		task = new ContainerTask(this.getName());
		synchronized (all) {
			for (IAlgoExecution e: all) {
				task.addTask(e);
			}
		}
		*/

		
		// init progress
		// TODO remove
		//progress.setComputationState(ComputationState.STARTED);
		//progress.setProgressTotal(1);
		
		// register listener
		registerListeners();
		
		// init internal structures 
		synchronized (all) {
				
			// at init: 
			// - nothing is done
			done.clear();
			
			// - roots are ready
			ready.clear();
			ready.addAll(detectRoots());
			
			// - nothing is running
			running.clear();
			
			// - all others are not ready
			notReady.clear();
			notReady.addAll(all);
			notReady.removeAll(ready);
		
		}
		
	}
	
	protected void printState() {
		
		StringBuffer sb = new StringBuffer();
		
		synchronized (all) {
			sb
				.append(done.size()).append(" done, ")
				.append(running.size()).append(" running, ")
				.append(ready.size()).append(" ready, ")
				.append(notReady.size()).append(" waiting.");
			
		}
		
		messagesRun.traceTech(sb.toString(), getClass());
		
	}
	
	protected boolean proposeDynamicProducersToWork() {
		
		synchronized (tasksProducers) {
			
			if (tasksProducers.isEmpty())
				return false;
			
			messagesRun.debugTech("proposing to tasks producers to submit novel tasks...", getClass());
			
			Iterator<ITasksDynamicProducer> itProducers = tasksProducers.iterator();
			while (itProducers.hasNext()) {
				
				ITasksDynamicProducer producer = itProducers.next();

				if (!producer.willMoreTasks()) {
					// this producer has no more work; let's leave him alone
					itProducers.remove();
					messagesRun.debugTech("task producer "+producer+" has no more tasks", getClass());
					continue;
				}
				
				if (producer.cannotSendTasksNow()) {
					// this producer will send tasks later, but not now
					// just don't focus on it
					continue;
				}

				messagesRun.debugTech("proposing to task producer "+producer+" to submit novel tasks...", getClass());
					
				IAlgoExecution t = null;
				
				try {
					t = producer.provideMoreTasks();
				} catch (RuntimeException e) {
					messagesRun.errorTech("error while proposing a producer to submit jobs:"+e.getMessage(), getClass(), e);
					// TODO invalidate something !
					// TODO change tasks producer so it provides the info about its container task
				}
				
				if (t == null) {
					tasksProducers.remove(producer);
					messagesRun.debugTech("task producer "+producer+" has no more tasks", getClass());
					return false;
				} else {
					addTask(t);
					return true;
				}
				
			}
			
			return false;
			
			
		}
	}
	
	
	private void mayCleanTasks(long mintimeMs) {
		
		// should we clean ? 
		if (cleanable.size() < 10)
			return;

		messagesRun.debugTech("we are doing a bit of housekeeping there :-) ("+cleanable.size()+" tasks ready for cleaning)", getClass());
		
		synchronized (all) {
			
			long minimalTimestamp = System.currentTimeMillis() - mintimeMs; // don't clean tasks which are not done since mintime
					
			Iterator<ICleanableTask> itSub = cleanable.iterator();
			while (itSub.hasNext()) {
				ICleanableTask sub = itSub.next();
				
				// sometimes we attempt to clean the tasks a bit too quickly
				// just don't.
				if (sub.getProgress().getTimestampEnd() > minimalTimestamp)
					continue;
					
				try {
					messagesRun.traceTech("cleaning task: "+sub, getClass());

					// now clean it !
					
					sub.clean();
					
				} catch (RuntimeException e) {
					messagesRun.warnTech("oops,  catched an error while attempting to clean a task: "+sub, getClass(), e);
					e.printStackTrace();
				}
				
				itSub.remove();

				
			}
			
		}
		
		
	}
	
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#run()
	 */
	@Override
	public void run() {
		
		boolean displayMessage = false;
		
		initRun();
	
	
		while (!cancel) {
			
			long timeoutToWait;
			if (notReady.isEmpty() && running.isEmpty() && tasksProducers.isEmpty())
				timeoutToWait = 0; // nothing can happen. Let's just wait forever (until wake up)
			else
				timeoutToWait = 500;
		
			try {
				messagesRun.traceTech("nothing to do, sleeping.", getClass());
				synchronized (lockerMainLoop) {
					lockerMainLoop.wait(timeoutToWait);	
				}
				messagesRun.traceTech("wake up !", getClass());
			} catch (InterruptedException e) {
				messagesRun.traceTech("Runner thread interrupted o_O. This is not supposed to happen.", getClass());
				e.printStackTrace();
			}
			
			// search more work
			synchronized (all) {
			
				// if some need more work
				if (ready.isEmpty()) {
					// ... ask the dynamic producers to submit some
					proposeDynamicProducersToWork();
				}
				

			}
			
			// clean old work
			mayCleanTasks(2000);
			
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#cancelTasks()
	 */
	public void cancelTasks() {
		cancel = true;
		
		synchronized (all) {
			for (IAlgoExecution e: all) {
				switch (e.getProgress().getComputationState()) {
				case CREATED:
				case READY:
				case STARTED:
				case WAITING_DEPENDENCY:
					//e.getProgress().setComputationState(ComputationState.FINISHED_CANCEL);	
					e.cancel();
				}
				
			}
		}
	}

	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#computationStateChanged(genlab.core.model.exec.IComputationProgress)
	 */
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		
		//messages.traceTech("computation state changed: "+progress.getAlgoExecution()+": "+progress.getComputationState(), getClass());
		
		IAlgoExecution e = progress.getAlgoExecution();

		boolean wakeUp = false;
		
		final ListOfMessages messages = e.getExecution().getListOfMessages();
		
		synchronized (all) {
	
			// check event
			if (!all.contains(e)) {
				all.add(e);
			}
				//throw new ProgramException("oops, I receive events which are not really of interest to me, like: "+progress.getAlgoExecution().getAlgoInstance());
			
			switch (progress.getComputationState()) {
			
			// a task was running, and ended
			case FINISHED_FAILURE:
				// cancelTasks();
			case FINISHED_OK:
			case FINISHED_CANCEL:
				messages.traceTech("task finished: "+e+" ("+progress.getDurationMs()+" ms)", getClass());
				if (running.contains(e)) {
					running.remove(e);
					done.add(e);
					
					if (e.getThreadsUsed() == 0) {
						usedThreadsWithoutThread--;
					} else {
						usedThreads -= e.getThreadsUsed();
					}
		
				}
				possibilityOfTaskCleanup(e);
				wakeUp = true;
				break;
				
			// a task was waiting for dependancy, and received all inputs
			case READY:
				messages.traceTech("task is now ready: "+e, getClass());
				notReady.remove(e);
				ready.add(e);
				submitTaskToWorkerThreads(e);
				wakeUp = true;
				break;
			
			case STARTED:
				ready.remove(e);
				notReady.remove(e);
				if (running.add(e)) {
					if (e.getThreadsUsed() == 0) {
						usedThreadsWithoutThread++;
					} else {
						usedThreads += e.getThreadsUsed();
					}
				}
				break;
				
			case CREATED:
			case WAITING_DEPENDENCY:	
				// ignore;
				break;
				
			default:
				throw new ProgramException("unknown state: "+progress.getComputationState());
			}
		
		}
		
		if (wakeUp)
			wakeUp();
		
		printState();

	}

	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#cancel()
	 */
	@Override
	public void cancel() {
		cancelTasks();
		
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#kill()
	 */
	@Override
	public void kill() {
		// TODO
		cancelTasks();
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#registerTasksDynamicProducer(genlab.core.exec.ITasksDynamicProducer)
	 */
	@Override
	public void registerTasksDynamicProducer(ITasksDynamicProducer producer) {
		synchronized (tasksProducers) {
			messagesRun.debugTech("registered a novel tasks producer: "+producer, getClass());
			tasksProducers.add(producer);
		}
		wakeUp();
	}

	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#taskCleaning(genlab.core.exec.ITask)
	 */
	@Override
	public void taskCleaning(ITask task) {
		
		// a task is going to be removed
		// I should no more care about this task !
		synchronized (all) {
			if (running.contains(task))
				throw new ProgramException("should not clean a task which is still in running state !");
			
			all.remove(task);
			done.remove(task);
			running.remove(task);
			ready.remove(task);
			roots.remove(task);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#possibilityOfTaskCleanup(genlab.core.exec.ITask)
	 */
	@Override
	public void possibilityOfTaskCleanup(ITask task) {
		
		// only clean the tasks marked as "you may clean me"
		if (!(task instanceof ICleanableTask))
			return;
		
		cleanable.add((ICleanableTask)task);
	}
	
}
