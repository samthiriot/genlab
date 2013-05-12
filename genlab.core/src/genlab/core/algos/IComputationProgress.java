package genlab.core.algos;

/**
 * Describes the evolution of a computation.
 * 
 * @author Samuel Thiriot
 */
public interface IComputationProgress {

	
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
	
}
