package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;

/**
 * For its execution, a constant value takes as a parameter the output to update, the value to output. 
 * 
 * @author Samuel Thiriot
 *
 * @param <JavaType>
 */
public class ConstantValueExecution<JavaType> extends AbstractAlgoExecution {

	protected JavaType value = null;
	
	public ConstantValueExecution(
			IExecution exec,
			IAlgoInstance algoInst,
			JavaType value
			) {
		super(
				exec,
				algoInst, 
				new ComputationProgressWithSteps()
				);
		this.value = value;
	}

	@Override
	public void run() {

		// notify of the start of the task
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		// define result
		ComputationResult result = new ComputationResult(algoInst, progress);
		IInputOutput<JavaType> output = ((ConstantValue<JavaType>)algoInst.getAlgo()).getOutput(); 
		result.setResult(
				algoInst.getOutputInstanceForOutput(output), 
				value
				);

		// and, of the end.
		setResult(result);
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}
	
	@Override
	public boolean isCostless() {
		return true;
	}

}