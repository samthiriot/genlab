package genlab.gephi.utils;

import genlab.core.model.exec.IComputationProgress;

import org.gephi.utils.progress.ProgressTicket;

/**
 * 
 * A gephi progress ticket which receives progress events from gephi; 
 * so it can forward them to the genlab progress passed as parameter. 
 * 
 * @author Samuel Thiriot
 *
 */
public final class ProgressTicketGephiToGenlab implements ProgressTicket {

	private final IComputationProgress glProgress;
	
	// we keep the original progress elements, so we will not erase previous informations
	private final long glDoneStartup;
	private final long glToDoStartup;
	
	// we also monitor the last info received; we will not update every iteration (could be quiet long)*
	private int nextProgressToRelay = -1; 
	
	private int totalToDoStored = 1000;
	
	/**
	 * The minimal step required for us to relay the novel information 
	 * (we will not relay a "+1" every ms) 
	 */
	private int minStepToRelay = 10;
	
	/**
	 * The total to do transmitted by the Gephi algo will be divided in this count of slices.
	 * Only these slices will be transmitted to genlab.
	 */
	private final static int PROGRESS_STEPS_DIVIDEND = 500;
	
	public ProgressTicketGephiToGenlab(IComputationProgress glProgress) {
		
		this.glProgress = glProgress;
		
		glDoneStartup = glProgress.getProgressDone();
		glToDoStartup = glProgress.getProgressTotalToDo();
		
		
	}
	
	protected void processToDoFromGephi(int todo) {
		
		this.totalToDoStored = todo;
		
		//System.err.println("updating progress todo: "+todo);
		glProgress.setProgressTotal(glToDoStartup+todo);
		
		minStepToRelay = Math.max(1, todo/PROGRESS_STEPS_DIVIDEND);
		//System.err.println("min step: "+minStepToRelay);
	}
	
	protected void processDoneFromGephi(int done) {
		
		if (done < nextProgressToRelay) 
			// not enough change; 
			// we will not disturb our event chain for that
			return;
		
		// update :-)
		//System.err.println("updating progress done: "+done);
		glProgress.setProgressMade(glDoneStartup+done);
		
		// plan next value
		nextProgressToRelay = done + minStepToRelay;
		
	}
	
	protected void processFinishFromGephi() {
		// nota: this never happened during our tests ^^
		//System.err.println("gephi send: done");
		processDoneFromGephi(totalToDoStored);
	}

	@Override
	public void finish() {
		processFinishFromGephi();
	}

	@Override
	public void finish(String arg0) {
		processFinishFromGephi();
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public void progress() {
	}

	@Override
	public void progress(int arg0) {
		processDoneFromGephi(arg0);
	}

	@Override
	public void progress(String arg0) {
		
	}

	@Override
	public void progress(String arg0, int arg1) {
		processDoneFromGephi(arg1);
	}

	@Override
	public void setDisplayName(String arg0) {
	}

	@Override
	public void start() {
	}

	@Override
	public void start(int arg0) {
		processToDoFromGephi(arg0);
	}

	@Override
	public void switchToDeterminate(int arg0) {
		processToDoFromGephi(arg0);
	}

	@Override
	public void switchToIndeterminate() {

	}

}
