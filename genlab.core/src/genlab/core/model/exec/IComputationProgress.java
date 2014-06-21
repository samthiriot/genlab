package genlab.core.model.exec;

import java.util.Set;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.exec.ITask;
import genlab.core.model.meta.IAlgo;

/**
 * Describes the evolution of a computation.
 * 
 * @author Samuel Thiriot
 */
public interface IComputationProgress {

	
	public void _setAlgoExecution(IAlgoExecution exec);

	public IAlgoExecution getAlgoExecution();

	/**
	 * 	The timestamp of the end of the computation, or null 
	 * if not finished
	 */
	public Long getTimestampEnd();
	
	/**
	 * The timestamp of the beginning of the computation,
	 * or null if not started
	 * @return
	 */
	public Long getTimestampStart();
	 
	public UniqueTimestamp getTimestampCreation();

	/**
	 * Returns the duration of the task in milliseconds,
	 * or null if not available (either not started or not ended)
	 * @return
	 */
	public Long getDurationMs();
	
	/**
	 * Returns the count of threads used for computation
	 * @return
	 */
	public int getThreadUsed();
	
	/**
	 * Returns true if the progress can be monitored (like 1%, 2%...100%)
	 * @return
	 */
	public boolean hasProgress();
	
	/**
	 * Returns the progress of the computation as a percentage;
	 * Returns null if not progress known.
	 * @return
	 */
	public Double getProgressPercent();
	
	/**
	 * Returns the total iterations to be run;
	 * not that this number could evolve during the computation !
	 * @return
	 */
	public Long getProgressTotalToDo();
	
	/**
	 * Returns the total iterations made;
	 * always less than getProgressTotalToDo()
	 * @return
	 */
	public Long getProgressDone();
	
	/**
	 * Returns the algo which actually computes everything.
	 * @return
	 */
	public IAlgo getAlgo();
	
	public void setProgressTotal(Long total);
	
	public void setProgressMade(Long total);
	
	public void setProgressTotal(Integer total);
	
	public void setProgressMade(Integer total);
	
	public void incProgressMade(Long inc);
	
	public void incProgressMade(Integer inc);
	
	public void incProgressMade();
	
	public ComputationState getComputationState();
	
	public void setComputationState(ComputationState state);
	
	public void setException(Throwable exception);
	public Throwable getException();
	
	public void addListener(IComputationProgressSimpleListener listener);
	public void removeListener(IComputationProgressSimpleListener listener);
	
	public void addDetailedListener(IComputationProgressDetailedListener listener);
	public void removeDetailedListener(IComputationProgressDetailedListener listener);
	
	
	/**
	 * Defines a current activity
	 * @param name
	 */
	public void setCurrentTaskName(String name);
	

	public String getCurrentTaskName();
	
	public void clean();

	public void propagateRank(Integer rank, Set<ITask> visited);
}
