package genlab.core.exec;

import genlab.core.commons.ProgramException;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.exec.IComputationProgressSimpleListener;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Takes many tasks, and executes them.
 * 
 * TODO use the map of threads to monitor and kill
 * TODO timeouts
 * 
 * @author Samuel Thiriot
 *
 */
public class Runner extends Thread implements IComputationProgressSimpleListener {
	
	final static int MAX_THREADS = 10;
	
	final Set<IAlgoExecution> all = new HashSet<IAlgoExecution>();

	final Set<IAlgoExecution> roots = new HashSet<IAlgoExecution>();;

	final Set<IAlgoExecution> done = new HashSet<IAlgoExecution>();
	final Set<IAlgoExecution> ready = new HashSet<IAlgoExecution>();
	final Set<IAlgoExecution> running = new HashSet<IAlgoExecution>();
	final Set<IAlgoExecution> notReady = new HashSet<IAlgoExecution>();	
	
	final Map<IAlgoExecution, Thread> exec2thread = new HashMap<IAlgoExecution, Thread>();
	
	private int usedThreads = 0;
	
	IComputationProgress progress;
	
	boolean cancel = false;
	
	private ListOfMessages messages = null;
	
	private final IContainerTask task;
	
	private final IExecution execution;
	
	public Runner(IExecution execution, IComputationProgress progress, Collection<IAlgoExecution> allTasks, IContainerTask task) {
		
		this.progress = progress;
		
		this.execution = execution;
		messages = execution.getListOfMessages();
		
		for (IAlgoExecution e: allTasks)
			addTask(e);
	
		
		this.task = task;
		
		setName("glRunner");
		setPriority(MAX_PRIORITY);
		setDaemon(true);
	}
	
	protected void addTask(IAlgoExecution exec) {
		
		all.add(exec);
		//ExecutionHooks.singleton.notifyParentTaskAdded(exec);

		
	}

	protected Collection<IAlgoExecution> detectRoots() {
		
		LinkedList<IAlgoExecution> res = new LinkedList<IAlgoExecution>();
		
		for (IAlgoExecution e: all) {
			if (e.getPrerequires().isEmpty() && e.getAlgoInstance().getContainer()==null)
				res.add(e);
		}
		
		messages.traceTech("found root tasks: "+res, getClass());
		
		return res;
		
	}
	
	protected void registerListeners() {
		synchronized (all) {
			for (IAlgoExecution e: all) {
				e.getProgress().addListener(this);
			}	
		}
	}
	
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
		TasksManager.singleton.notifyListenersOfTaskAdded(task);

		
		// init progress
		// TODO
		progress.setComputationState(ComputationState.STARTED);
		progress.setProgressTotal(1);
		
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
				.append(exec2thread.size()).append(" running, ")
				.append(ready.size()).append(" ready, ")
				.append(notReady.size()).append(" waiting.");
			
		}
		
		messages.traceTech(sb.toString(), getClass());
		
	}
	
	
	
	protected boolean attemptToDoSomething() {
		
		messages.traceTech("attempting to do something", getClass());
		
		printState();
		
		// are there enough resources ?
		if (usedThreads >= MAX_THREADS) {
			messages.debugTech("all threads used, wait...", getClass());
			return false;	// threads limit reached, do nothing.
		}
		
		// is there something ready ?
		IAlgoExecution e = null;
		Thread t = null;
		synchronized (all) {
			
			if (ready.isEmpty()) {
				messages.traceTech("nothing ready, wait...", getClass());
				if (running.isEmpty()) {
					// TODO wait, nothing is gonna happen there ?
					
				}
				return false; // nothing ready for run, leave.
			}
			
			e = ready.iterator().next();
			ready.remove(e);
			running.add(e);
		
			// actually run something
			if (!e.isCostless()) {
				t = new Thread(e);
				t.setName("gl_task");
				t.setDaemon(false);
				t.setPriority(NORM_PRIORITY);
				exec2thread.put(e, t);
				usedThreads += e.getThreadsUsed();
			}
		}
		
		if (t==null) {
			messages.debugUser("running task "+e+" (no thread, it is costless)", getClass());
			e.run();
		} else {
			messages.debugUser("starting a thread for task "+e+": "+t, getClass());
			final IAlgoExecution e2 = e;
			
			// add an exception handler to the thread;
			t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread arg0, Throwable arg1) {
					
					e2.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);

					arg1.printStackTrace();
					messages.errorUser(
							"this algorithm ended with an error: "+arg1.getMessage(), 
							getClass(), 
							arg1
							);
					messages.warnTech(
							"an algorithm raised an error that was not properly catched at its level: "+e2+", "+arg1.getClass(), 
							getClass(),
							arg1
							);
				}
			});
			
			// also add a watchdog
			/* TODO restore the watchdog
			(new WatchdogTimer(
					e2.getTimeout(), 
					e2.getProgress(), 
					e2.getExecution().getListOfMessages())
			).start();
			*/
			
			t.start();
		}
		
		printState();

		
		return true;
		
	}
	
	public void attemptToDoThings() {
		
		while (attemptToDoSomething()) {}
		
	}
	
	public void run() {
		
		initRun();
	
		attemptToDoThings();
	
		while (!cancel) {
			synchronized (all) {
				if (running.isEmpty() && ready.isEmpty() && notReady.isEmpty()) {
					messages.infoUser("all tasks done", getClass());
					execution.displayTechnicalInformationsOnMessages();
					return;
				}
			}
			try {
				//GLLogger.debugTech("wait", getClass());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void cancelTasks() {
		cancel = true;
		
		synchronized (all) {
			for (IAlgoExecution e: all) {
				switch (e.getProgress().getComputationState()) {
				case CREATED:
				case READY:
				case STARTED:
				case WAITING_DEPENDENCY:
					e.getProgress().setComputationState(ComputationState.FINISHED_CANCEL);	
				}
				
			}
		}
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		//messages.traceTech("computation state changed: "+progress.getAlgoExecution()+": "+progress.getComputationState(), getClass());
		
		IAlgoExecution e = progress.getAlgoExecution();
	
		// check event
		if (!all.contains(e))
			throw new ProgramException("oops, I receive events which are not really of interest to me !");
		
		switch (progress.getComputationState()) {
		
		// a task was running, and ended
		case FINISHED_FAILURE:
			cancelTasks();
		case FINISHED_OK:
		case FINISHED_CANCEL:
			messages.traceTech("task finished: "+e+" ("+progress.getDurationMs()+" ms)", getClass());
			synchronized (all) {
				running.remove(e);
				done.add(e);
				exec2thread.remove(e);
				usedThreads -= e.getThreadsUsed();
			}
			attemptToDoThings();
			break;
			
		// a task was waiting for dependancy, and received all inputs
		case READY:
			messages.traceTech("task is now ready: "+e, getClass());
			synchronized (all) {
				notReady.remove(e);
				ready.add(e);
			}
			attemptToDoThings();
			break;
			
		case WAITING_DEPENDENCY:
		case STARTED:
		case CREATED:
			// ignore;
			break;
			
		default:
			throw new ProgramException("unknown state: "+progress.getComputationState());
		}
		
	}

	public void cancel() {
		cancelTasks();
		
	}
	
	public void kill() {
		// TODO
		cancelTasks();
	}
}
