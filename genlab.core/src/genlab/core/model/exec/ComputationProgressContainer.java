package genlab.core.model.exec;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.ITask;
import genlab.core.model.meta.IAlgo;

public class ComputationProgressContainer implements IComputationProgress {

	private IContainerTask algoExec = null;
	
	public ComputationProgressContainer() {
	}

	@Override
	public void _setAlgoExecution(IAlgoExecution exec) {

		algoExec = (IContainerTask)exec;
	}

	@Override
	public IAlgoExecution getAlgoExecution() {
		return (IAlgoExecution)algoExec;
	}

	@Override
	public Long getTimestampEnd() {
		
		return null;
	}

	@Override
	public Long getTimestampStart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UniqueTimestamp getTimestampCreation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDurationMs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getThreadUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasProgress() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Double getProgressPercent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getProgressTotalToDo() {
		long total = 0;
		for (ITask sub: algoExec.getTasks()) {
			total += sub.getProgress().getProgressTotalToDo();
		}
		return total;
	}

	@Override
	public Long getProgressDone() {
		long done = 0;
		for (ITask sub: algoExec.getTasks()) {
			done += sub.getProgress().getProgressDone();
		}
		return done;
	}

	@Override
	public IAlgo getAlgo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProgressTotal(Long total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProgressMade(Long total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProgressTotal(Integer total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProgressMade(Integer total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incProgressMade(Long inc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incProgressMade(Integer inc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incProgressMade() {
		// TODO Auto-generated method stub

	}

	@Override
	public ComputationState getComputationState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComputationState(ComputationState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(IComputationProgressSimpleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(IComputationProgressSimpleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCurrentTaskName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCurrentTaskName() {
		// TODO Auto-generated method stub
		return null;
	}

}
