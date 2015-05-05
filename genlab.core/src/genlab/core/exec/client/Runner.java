package genlab.core.exec.client;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.ICleanableTask;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IRunner;
import genlab.core.exec.ITask;
import genlab.core.exec.ITasksDynamicProducer;
import genlab.core.exec.TasksManager;
import genlab.core.exec.WorkingRunnerThread;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
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
	
	
	final static int START_TASKS_SIZE = 500;
	
	
	/**
	 * Acts as the locker for: all, roots, done, ready, running, notReady
	 */
	final HashSet<IAlgoExecution> all = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	
	final HashSet<ICleanableTask> cleanable = new HashSet<ICleanableTask>(START_TASKS_SIZE);

	final HashSet<IAlgoExecution> roots = new HashSet<IAlgoExecution>();;

	final HashSet<IAlgoExecution> done = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final HashSet<IAlgoExecution> ready = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final HashSet<IAlgoExecution> running = new HashSet<IAlgoExecution>(START_TASKS_SIZE);
	final HashSet<IAlgoExecution> notReady = new HashSet<IAlgoExecution>(START_TASKS_SIZE);	
	

	final HashSet<ITasksDynamicProducer> tasksProducers = new HashSet<ITasksDynamicProducer>();
	
	

		
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
	protected Set<Thread> threadsPoolTaskNoThread = new HashSet<Thread>(500);
	final BlockingQueue<IAlgoExecution> readyToComputeNoThread = new LinkedBlockingQueue<IAlgoExecution>();
	private int usedThreadsWithoutThread = 0;

	/**
	 * Contains worker threads which do use the CPU. Contains only MAX_THREADS
	 */
	protected Set<Thread> threadsPoolTaskWithThreads = new HashSet<Thread>(500);
	protected final BlockingQueue<IAlgoExecution> readyToComputeWithThreads = new LinkedBlockingQueue<IAlgoExecution>();

	/**
	 * Contains worker threads which delegate to distant servers.
	 */
	protected final BlockingQueue<IAlgoExecutionRemotable> readyToComputeRemotable = new LinkedBlockingQueue<IAlgoExecutionRemotable>();

	
	/**
	 * The number of threads used currently. Based on the number of thread displayed by tasks, 
	 * not an objective measure.
	 */
	private int usedThreads = 0;

	private int availableThreads = 4;
	
	public Runner(int availableLocalThreads) {
		
		this.messagesRun =  ListsOfMessages.getGenlabMessages();
		
		this.availableThreads = availableLocalThreads;
		
		messagesRun.infoUser("creating local runner (threads = "+availableLocalThreads+")", getClass());
		
		setName("glRunner");
		setPriority(MIN_PRIORITY);
		setDaemon(true);
		

		// init the thread pool
		for (int i=0; i<availableLocalThreads; i++) {
			WorkingRunnerThread t = new WorkingRunnerThread(
					"gl_worker_local_"+i, 
					readyToComputeWithThreads,
					readyToComputeRemotable
					);
			addWorkingThread(t);
		}
		
		// init the thread pool
		for (int i=0; i<availableLocalThreads; i++) {
			WorkingRunnerThread t = new WorkingRunnerThread(
					"gl_workcontroller_local_"+i, 
					readyToComputeNoThread,
					null
					);
			t.start();
			threadsPoolTaskNoThread.add(t);
		}
		
		// notify the work of our existence
		TasksManager.singleton.addRunner(this);
	}
	
	protected void addWorkingThread(Thread thread) {
		
		synchronized (threadsPoolTaskWithThreads) {
			messagesRun.infoTech("adding working thread: "+thread.getName(), getClass());
			
			thread.start();
			threadsPoolTaskWithThreads.add(thread);
		}
	}

	private void removeWorkingThread(WorkingRunnerDistanceThread thread) {
		
		synchronized (threadsPoolTaskWithThreads) {
			messagesRun.infoTech("removing working thread: "+thread.getName(), getClass());
			
			threadsPoolTaskWithThreads.remove(thread);
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
	 *  Directs a task to the queue of distant execution or falls back to 
	 * standard behaviour.
	 * 
	 * @param exec
	 */
	protected void submitTaskToWorkerThreads(IAlgoExecution exec) {
		
		if (exec.getThreadsUsed() == 0) {
			// control thread, let's just delegate it there
			synchronized (threadsPoolTaskNoThread) {
				if (usedThreadsWithoutThread == threadsPoolTaskNoThread.size()) {
					messagesRun.debugTech("not enough threads; increasing the size to "+(threadsPoolTaskNoThread.size()+1), getClass());
					threadsPoolTaskNoThread.add(
							new WorkingRunnerThread(
									"gl_workcontroller_local_"+threadsPoolTaskNoThread.size(), 
									readyToComputeNoThread,
									null
									)
							);
				}
				readyToComputeNoThread.add(exec);

			}
		} else if (exec instanceof IAlgoExecutionRemotable) {
			// if it can be executable remotely, here is its target
			readyToComputeRemotable.add((IAlgoExecutionRemotable) exec);
		} else {
			// execute locally only
			readyToComputeWithThreads.add(exec);
		}
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#addTask(genlab.core.model.exec.IAlgoExecution)
	 */
	@Override
	public void addTask(IAlgoExecution exec) {
		
		messagesRun.debugTech("adding task: "+exec+" (state "+exec.getProgress().getComputationState()+")", getClass());
		
		synchronized (all) {
			
			if (all.contains(exec))
				return;
			
			exec.getProgress().addListener(this);

			all.add(exec);
			
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
		
		// add subtasks
		if (exec instanceof IContainerTask) {
			IContainerTask execContainer = (IContainerTask)exec;
			if (!execContainer.getTasks().isEmpty()) {
				for (ITask subtask: execContainer.getTasks()) {
					addTask((IAlgoExecution) subtask);
				}
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
		
		messagesRun.debugTech("detecting the roots of tasks", getClass());
		
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
	
	public final String getHumanReadableState() {

		StringBuffer sb = new StringBuffer();
		
		synchronized (all) {
			sb
				.append(done.size()).append(" done, ")
				.append(running.size()).append(" running, ")
				.append(ready.size()).append(" ready, ")
				.append(notReady.size()).append(" waiting.")
				//.append("\n(Running: ").append(running.toString()).append(")")
				//.append("\n(Ready: ").append(ready.toString()).append(")")
				//.append("\n(Pending: ").append(notReady.toString()).append(")")
				;
			
			
		}
		
		return sb.toString();
	}
	
	protected void printState() {
		
		messagesRun.traceTech(getHumanReadableState(), getClass());
		
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
		if (cleanable.size() < 20)
			return;

		messagesRun.debugTech("we are doing a bit of housekeeping there :-) ("+cleanable.size()+" tasks ready for cleaning)", getClass());
		
		synchronized (all) {
								
			Iterator<ICleanableTask> itSub = cleanable.iterator();
			while (itSub.hasNext()) {
				ICleanableTask sub = itSub.next();

				try {
					
					if (!sub.isCleanable())
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

				} catch (RuntimeException e) {
					// don't care about cleaning
					messagesRun.warnTech("exception while attempting to clean a task", getClass());
					System.err.println("error when cleaning task "+sub);
					e.printStackTrace();
				}
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
				timeoutToWait = 5000; // nothing can happen. Let's just wait forever (until wake up)
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
		
		messagesRun.debugTech("asked to cancel all tasks", getClass());

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
	
	public final int getCountPending() {
		return notReady.size();
	}
	
	public final int getCountReady() {
		return ready.size();
	}
	
	public final int getCountRunning() {
		return running.size();
	}

	public final int getCountDone() {
		return done.size();
	}


	public final int getCountNotFinished() {
		return running.size() + ready.size() + notReady.size();
	}
	
	

	/* (non-Javadoc)
	 * @see genlab.core.exec.IRunner#computationStateChanged(genlab.core.model.exec.IComputationProgress)
	 */
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		IAlgoExecution e = progress.getAlgoExecution();

		boolean wakeUp = false;
		
		final ListOfMessages messages = e.getExecution().getListOfMessages();
		
		messages.traceTech("receiving state change: "+progress.getAlgoExecution()+" is now "+progress.getComputationState(), getClass());

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
				
				// remove this task from its previous state
				boolean wasRunning = running.remove(e);
				boolean wasContained = wasRunning || ready.remove(e) || notReady.remove(e) || done.remove(e);
				
				// and if we knew it, then add it to "done" tasks
				if (wasContained) {
					done.add(e);
				}
				if (wasRunning) {
					
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
				messages.traceTech("task started: "+e, getClass());
				ready.remove(e);
				notReady.remove(e);
				if (running.add(e)) {
					if (e.getThreadsUsed() == 0) {
						usedThreadsWithoutThread++;
					} else {
						usedThreads += e.getThreadsUsed();
					}
				}
				wakeUp = ready.isEmpty();
					
				break;
				
			case SENDING_CONTINOUS:
				// ignore
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
		
		messagesRun.traceTech("notified of the possibility to clean a task: "+task, getClass());

		// only clean the tasks marked as "you may clean me"
		if (!(task instanceof ICleanableTask))
			return;
		
		cleanable.add((ICleanableTask)task);
	}


	@Override
	public boolean containsTask(IAlgoExecution exec) {
		synchronized (all) {
			return all.contains(exec);	
		} 
	}


	@Override
	public Collection<IAlgoExecution> getAllTasks() {
		synchronized (all) {
			return (Collection<IAlgoExecution>) all.clone();
		} 
	}


	@Override
	public void propagateRank(Integer rank, Set<ITask> visited) {
		// not relevant
	}


	public void addRunnerDistant(WorkingRunnerDistanceThread thread) {
		
		try {
			
			addWorkingThread(thread);
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO error !
		}
	}

	public void removeRunnerDistant(WorkingRunnerDistanceThread thread) {
		try {
			removeWorkingThread(thread);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	

	
}
