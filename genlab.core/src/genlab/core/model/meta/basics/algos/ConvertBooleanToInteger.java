package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

public class ConvertBooleanToInteger extends BasicAlgo {


	public static final InputOutput<Boolean> INPUT = new InputOutput<Boolean>(
			BooleanFlowType.SINGLETON, 
			"in_bool", 
			"boolean", 
			"the boolean input"
			);

	
	public static final InputOutput<Integer> OUTPUT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_integer", 
			"integer", 
			"1.0 if the input is true, 0.0 if false"
			);

	public ConvertBooleanToInteger() {
		
		super(
				"Boolean to integer", 
				"converts a boolean value to a Integer value: true is 1.0, false is 0.0", 
				ExistingAlgoCategories.CASTING, 
				null, 
				null
				);
		
		inputs.add(INPUT);
		outputs.add(OUTPUT);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void run() {
				
				progress.setComputationState(ComputationState.STARTED);
				
				Boolean inValue = (Boolean)getInputValueForInput(INPUT);
				
				Integer result = null;
				if (inValue)
					result = 1;
				else
					result = 0;
				
				ComputationResult res = new ComputationResult(algoInst, progress, messages);
				res.setResult(OUTPUT, result);
				setResult(res);
				
				progress.setComputationState(ComputationState.FINISHED_OK);
				
				
			}
			
			@Override
			public long getTimeout() {
				return 1000;
			}
		};
	}

}
