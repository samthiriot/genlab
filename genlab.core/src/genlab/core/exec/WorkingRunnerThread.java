package genlab.core.exec;

import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.concurrent.BlockingQueue;

/**
 * This thread consumes tasks provided in one or two queues passed as parameters. 
 * It waits until an element comes; executes the task in its independent and local thread; 
 * then waits for the next element to process, etc. 
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkingRunnerThread extends Thread {

	
	/**
	 * The first queue to pick things from.
	 * If something is available on this queue, it should be processed first. 
	 */
	private final BlockingQueue<IAlgoExecution> readyToCompute;
	
	/**
	 * The second queue to pick things from. 
	 * If something is available on this queue and nothing in the first one, 
	 * then this task should be processed.
	 */
	private final BlockingQueue<? extends IAlgoExecution> readyToCompute2;
	
	/**
	 * A locker object which is notified when one of the queues is 
	 * changed, so the thread can wake up and check both
	 */
	private final Object lockOfQueues;
	
	private final ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	/**
	 * The current execution 
	 */
	protected IAlgoExecution exec = null;
	
	public WorkingRunnerThread(
				String name, 
				BlockingQueue<IAlgoExecution> readyToCompute, 
				BlockingQueue<? extends IAlgoExecution> readyToCompute2,
				Object lockOfQueues) {
		
		super(name);
		
		// check parameters
		assert readyToCompute2 == null || lockOfQueues != null;
		
		// store parameters
		this.readyToCompute = readyToCompute;
		this.readyToCompute2 = readyToCompute2;
		this.lockOfQueues = lockOfQueues;
		
		// parameters for this Thread
		setDaemon(true);
		setPriority(MIN_PRIORITY);
	}
	
	public IAlgoExecution getCurrentlyProcessedTask() {
		return exec;
	}

	@Override
	public void run() {

		while (true) {
			
			// find a task (maybe wait for it)
			if (this.readyToCompute2 == null) {
				// we can only consume from one principal queue
				// then just wait for it.
				try {
					messages.traceTech(this.getName()+" waiting for a task in the unique queue", getClass());
					exec = readyToCompute.take();
				} catch (InterruptedException e) {
					messages.errorTech("catched an exception when trying to fetch a task: "+e.getMessage(), getClass(), e);
				}
			} else {
				// we can consume from 2 queues.
				
				while (exec == null) {
					
					// is there something in the first one ? 
					exec = readyToCompute.poll();
					// or in another place ?
					if (exec == null) { 
						exec = readyToCompute2.poll();
					}
					// or wait !
					if (exec == null) { 
						// wait for something to be available on one queue
						synchronized (this.lockOfQueues) {
							try {
								this.lockOfQueues.wait();
							} catch (InterruptedException e) {
								messages.errorTech("catched an exception when waiting for a task: "+e.getMessage(), getClass(), e);
							}	
						}
					}
						
				}
				
			}
			
			
			// and run this task
			messages.debugTech(getName()+" running task: "+exec.getName(), getClass());
			try {
				exec.run();
			} catch (Exception e) {
				messages.errorUser("task "+exec.getName()+" raised an error:"+e.getMessage(), getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				exec.getProgress().setException(e);
			} catch (OutOfMemoryError e) {
				messages.errorUser("no more memory while processing task "+exec.getName()+"; update the memory settings", getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				exec.getProgress().setException(e);
			}
			messages.debugTech(getName()+" ran task: "+exec.getName(), getClass());
			
			exec = null;
			
			// might let the other react
			Thread.yield();
			
		}

	}
	
	

	
}
