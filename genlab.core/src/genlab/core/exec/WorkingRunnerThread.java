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
	
	private final ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	
	public WorkingRunnerThread(
				String name, 
				BlockingQueue<IAlgoExecution> readyToCompute, 
				BlockingQueue<? extends IAlgoExecution> readyToCompute2) {
		
		super(name);
		
		this.readyToCompute = readyToCompute;
		this.readyToCompute2 = readyToCompute2;
		
		setDaemon(true);
		setPriority(MIN_PRIORITY);
	}

	@Override
	public void run() {

		while (true) {
		
			IAlgoExecution exec = null;
			
			// find a task (maybe wait for it)
			if (this.readyToCompute2 == null) {
				// we can only consume from one principal queue
				// then just wait for it.
				try {
					//messages.infoTech(this.getName()+" waiting for a task in the unique queue", getClass());
					exec = readyToCompute.take();
				} catch (InterruptedException e) {
					messages.errorTech("catched an exception when trying to fetch a task: "+e.getMessage(), getClass(), e);
				}
			} else {
				// we can consume from 2 queues.
				do {
					//messages.infoTech(this.getName()+" searching for a task in the 2 queues", getClass());
					// is there something in the first one ? 
					exec = readyToCompute.poll();
					// or in another place ?
					if (exec == null) { 
						exec = readyToCompute2.poll();
					}
					// if there is nothing, loop until something comes.
					//messages.infoTech(this.getName()+" sleeping", getClass());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (exec == null);
				
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
			
		}

	}
	
	

	
}
