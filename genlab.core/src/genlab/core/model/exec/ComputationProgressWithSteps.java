package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.commons.UniqueTimestamp;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;

/**
 * TODO state
 * TODO timestamps
 * TODO relay small changes in progress ???
 * 
 * @author Samuel Thiriot
 *
 */
public class ComputationProgressWithSteps implements IComputationProgress, Cloneable {

	private IAlgoExecution algoExec = null;
	private UniqueTimestamp timestampCreation = null;
	private Long timestampStart = null;
	private Long timestampEnd  = null;
	private Long total  = null;
	private Long made  = null;
	private IAlgo algo = null;
	private ComputationState state = null;
	// locks the state. Used to avoid the state to be changed while we are dispatching the event on state change.
	private final Object stateLock = new Object();
	
	private String currentTaskName = "";
	
	protected LinkedList<IComputationProgressSimpleListener> listeners = new LinkedList<IComputationProgressSimpleListener>();
	protected LinkedList<IComputationProgressDetailedListener> listenersDetails = null;
	protected Object listenersDetailsLock = new Object();

	
	protected final static int MIN_DIFFERENCE_TO_DISPATCH_DETAILED_CHANGE = 5;
	
	public ComputationProgressWithSteps() {
		this.state = ComputationState.CREATED;
		this.timestampCreation = new UniqueTimestamp();
	}
	
	
	@Override
	public void _setAlgoExecution(IAlgoExecution exec) {
		this.algoExec = exec;
		this.algo = algoExec.getAlgoInstance().getAlgo();
		
	}

	@Override
	public Long getTimestampEnd() {
		return timestampEnd;
	}

	@Override
	public Long getTimestampStart() {
		return timestampStart;
	}

	@Override
	public Long getDurationMs() {
		if (timestampEnd == null || timestampStart == null)
			return null;
		else 
			return timestampEnd - timestampStart;
	}

	@Override
	public int getThreadUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasProgress() {
		return true;
	}

	@Override
	public Double getProgressPercent() {
		if (total == null || made == null)
			return null;
		return made*100.0/total;
	}

	@Override
	public Long getProgressTotalToDo() {
		return (total == null?0l:total); // TODO more efficient
	}

	@Override
	public Long getProgressDone() {
		return (made == null?0l:made); // TODO more efficient
	}

	@Override
	public IAlgo getAlgo() {
		return algo;
	}

	@Override
	public void setProgressTotal(Long total) {
		
		if (total != null && total <= 0)
			throw new ProgramException("total should be higher than 0");
		
		final long difference = (total == null || made == null?(total != made?1:0):total-this.made);

		this.total = total;
		
		if (total == null)
			made = null;
		
		else if (made == null)
			made = 0l;
		
		if (difference > MIN_DIFFERENCE_TO_DISPATCH_DETAILED_CHANGE)
			dispatchComputationStateChangedDetail();
	}

	@Override
	public void setProgressMade(Long total) {
		if (total < 0)
			throw new ProgramException("total should be higher than 0");
		
		final long difference = (total == null || made == null?(total != made?1:0):total-this.made);
		
		this.made = total;
		
		if (difference > MIN_DIFFERENCE_TO_DISPATCH_DETAILED_CHANGE)
			dispatchComputationStateChangedDetail();
	}

	@Override
	public void incProgressMade(Long inc) {
		this.made += inc;
	}

	@Override
	public void incProgressMade() {
		this.made ++;
	}

	@Override
	public void setProgressTotal(Integer total) {
		setProgressTotal(total.longValue());
	}

	@Override
	public void setProgressMade(Integer total) {
		setProgressMade(total.longValue());
	}

	@Override
	public void incProgressMade(Integer inc) {
		incProgressMade(inc.longValue());
	}

	@Override
	public ComputationState getComputationState() {
		return state;
	}

	@Override
	public void setComputationState(ComputationState state) {
		
		if (state == this.state)
			return; // quick exit
		
		if (state == null)
			throw new ProgramException("state should not be null");
		
		synchronized (stateLock) {

			switch (state) {
			case STARTED:
				this.timestampStart = System.currentTimeMillis();
				break;
			case FINISHED_FAILURE:
			case FINISHED_OK:
				this.made = this.total;
				this.timestampEnd = System.currentTimeMillis();
				break;
			}
			this.state = state;	

			dispatchComputationStateChanged();
		}
		
	}

	@Override
	public void addListener(IComputationProgressSimpleListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}	
	}
		

	@Override
	public void removeListener(IComputationProgressSimpleListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);	
		}
	}
	
	protected void dispatchComputationStateChanged() {
		LinkedList<IComputationProgressSimpleListener> listenersCopy = null; 
		
		synchronized (listeners) {
			listenersCopy = (LinkedList<IComputationProgressSimpleListener>) listeners.clone();
		}
		// produce a clone, so a change during dispatching would not raise a problem
		//ComputationProgressWithSteps clone = (ComputationProgressWithSteps) this.clone();
		for (IComputationProgressSimpleListener l:  listenersCopy) {
			try {
				l.computationStateChanged(this);
			} catch (RuntimeException e) {
				GLLogger.warnTech(
						"catched an exception while notifying listener "+l+" of computation state change ("+this+")", 
						getClass(), 
						e
						);
			}
		}	
		
	}
	
	protected void dispatchComputationStateChangedDetail() {
		LinkedList<IComputationProgressDetailedListener> listenersCopy = null; 
		
		if (listenersDetails == null)
			return;
		
		synchronized (listenersDetailsLock) {

			listenersCopy = (LinkedList<IComputationProgressDetailedListener>) listenersDetails.clone();
		}
		for (IComputationProgressDetailedListener l:  listenersCopy) {
			try {
				l.computationProgressChanged(this);
			} catch (RuntimeException e) {
				GLLogger.warnTech(
						"catched an exception while notifying listener "+l+" of computation progress change ("+this+")", 
						getClass(), 
						e
						);
			}
		}	
		
	}
	
	protected void dispatchCleaning() {
		
		if (listeners == null)
			return;
		
		LinkedList<IComputationProgressSimpleListener> listenersCopy = null; 
		
		synchronized (listeners) {
			listenersCopy = (LinkedList<IComputationProgressSimpleListener>) listeners.clone();
		}
		// produce a clone, so a change during dispatching would not raise a problem
		//ComputationProgressWithSteps clone = (ComputationProgressWithSteps) this.clone();
		for (IComputationProgressSimpleListener l:  listenersCopy) {
			try {
				l.taskCleaning(algoExec);
			} catch (RuntimeException e) {
				GLLogger.warnTech(
						"catched an exception while notifying listener "+l+" of task cleaning ("+this+")", 
						getClass(), 
						e
						);
			}
		}	
		
	}

	@Override
	public IAlgoExecution getAlgoExecution() {
		return algoExec;
	}


	@Override
	public void setCurrentTaskName(String name) {
		this.currentTaskName = name;
	}


	@Override
	public String getCurrentTaskName() {
		return currentTaskName;
	}


	@Override
	public UniqueTimestamp getTimestampCreation() {
		return timestampCreation;
	}

	public Object clone() {
		ComputationProgressWithSteps clone;
		
		try {
			clone = (ComputationProgressWithSteps)super.clone();
			
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}


	@Override
	public void clean() {
		
		if (listeners == null)
			return;
		
		// warn listeners
		dispatchCleaning();
		
		// clear local data
		listeners.clear();
		listeners = null;
		if (listenersDetails != null) {
			listenersDetails.clear();
			listenersDetails = null;
		}
		algo = null;
		algoExec = null;
		currentTaskName = null;
		made = null;
		state = null;
		
	}


	@Override
	public void addDetailedListener(IComputationProgressDetailedListener listener) {
		
		synchronized (listenersDetailsLock) {

			if (listenersDetails == null)
				listenersDetails = new LinkedList<IComputationProgressDetailedListener>();
			else if (listenersDetails.contains(listener))
				return;
			
			listenersDetails.add(listener);	
		}
	}


	@Override
	public void removeDetailedListener(IComputationProgressDetailedListener listener) {
		
		synchronized (listenersDetailsLock) {

			if (listenersDetails != null)
				listenersDetails.remove(listener);	
		}
	}
	
	


}
