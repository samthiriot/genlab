package genlab.core.model.exec;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;

/**
 * Easy monitoring of a task; checks the task finishes before a given timeout,
 * or defines it is dead.
 * 
 * @author Samuel Thiriot
 */
public class WatchdogTimer extends Thread implements IComputationProgressSimpleListener {

	private final long timeoutMs;
	
	private long timeStampEnd;
	
	private boolean canceled = false;
	
	private final IComputationProgress progress;
	
	private final ListOfMessages messages;
	
	public WatchdogTimer(long timeoutSeconds, IComputationProgress progress, ListOfMessages messages) {
		this.timeoutMs = timeoutSeconds;
		this.progress = progress;
		this.messages = messages;
	}
	
	/**
	 * Notifies the task was stopped by itself;
	 * it is no more required to wait.
	 */
	public void cancel() {
		canceled = true;
		messages.traceTech("cancelling this watchdog.", getClass());
		progress.removeListener(this);
		this.interrupt();
	}
	
	public void start() {
		
		progress.addListener(this);
		timeStampEnd = System.currentTimeMillis() + timeoutMs;
	
		super.start();
	}
	
	
	@Override
	public void run() {

		long toWait = timeoutMs;
		
		messages.traceTech("starting to watch task "+progress.getCurrentTaskName()+", timeout "+timeoutMs+"ms...", getClass());
		while (!canceled && toWait > 10) {
			try {
				Thread.sleep(toWait);
			} catch (InterruptedException e) {
				
			}
			toWait = timeStampEnd - System.currentTimeMillis();
		}
		
		if (!canceled) {
			messages.warnTech("timeout reached ("+timeoutMs+" ms), declaring the task in failure state.", getClass());
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			progress.removeListener(this);
		}
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		switch (progress.getComputationState()) {
		case FINISHED_CANCEL:
		case FINISHED_FAILURE:
		case FINISHED_OK:
			messages.traceTech("task finished by itself; cancelling watchdog...", getClass());
			cancel();
			break;
		default:
			// nothing to do.
		}
		
	}
	
}
