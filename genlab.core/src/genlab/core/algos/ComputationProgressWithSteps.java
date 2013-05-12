package genlab.core.algos;

import genlab.core.commons.ProgramException;

/**
 * TODO state
 * TODO timestamps
 * 
 * @author B12772
 *
 */
public class ComputationProgressWithSteps implements IComputationProgress {

	private Long timestampStart = null;
	private Long timestampEnd  = null;
	private Long total  = null;
	private Long made  = null;
	private final IAlgo algo;
	private ComputationState state = null;
	
	public ComputationProgressWithSteps(IAlgo algo) {
		this.algo = algo;
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
	}

}
