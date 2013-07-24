package genlab.core.model.exec;

import java.util.Collection;
import java.util.LinkedList;

import genlab.core.commons.ProgramException;
import genlab.core.commons.UniqueTimestamp;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;

/**
 * TODO state
 * TODO timestamps
 * 
 * @author B12772
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
	private Object stateLock = new Object();
	
	private String currentTaskName = "";
	
	protected Collection<IComputationProgressSimpleListener> listeners = new LinkedList<IComputationProgressSimpleListener>();
	
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
		return total;
	}

	@Override
	public Long getProgressDone() {
		return made;
	}

	@Override
	public IAlgo getAlgo() {
		return algo;
	}

	@Override
	public void setProgressTotal(Long total) {
		
		if (total != null && total <= 0)
			throw new ProgramException("total should be higher than 0");
		
		this.total = total;
		
		if (total == null)
			made = null;
		
		else if (made == null)
			made = 0l;
	}

	@Override
	public void setProgressMade(Long total) {
		if (total < 0)
			throw new ProgramException("total should be higher than 0");
		this.made = total;
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
		
		synchronized (stateLock) {

			this.state = state;	
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
			listenersCopy = new LinkedList<IComputationProgressSimpleListener>(listeners);
		}
		// produce a clone, so a change during dispatching would not raise a problem
		//ComputationProgressWithSteps clone = (ComputationProgressWithSteps) this.clone();
		for (IComputationProgressSimpleListener l:  listenersCopy) {
			l.computationStateChanged(this);
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


}
