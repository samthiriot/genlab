package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.AbstractExplorationParameter;

/**
 * The execution of an exploration parameter is a specific algo which transmits 
 * the value that was given to it. 
 * 
 * @author sam
 *
 */
public class ExplorationParameterExecution extends AbstractAlgoExecutionOneshot {

	private Object value = null;
	
	public ExplorationParameterExecution(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	public ExplorationParameterExecution() {
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void run() {
		progress.setComputationState(ComputationState.STARTED);

		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		res.setResult(
				((AbstractExplorationParameter)algoInst.getAlgo()).getExplorationOutput(), 
				((AbstractExplorationParameter)algoInst.getAlgo()).castValue(value)
				);
		
		setResult(res);
		progress.setComputationState(ComputationState.FINISHED_OK);

	}

	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

}
