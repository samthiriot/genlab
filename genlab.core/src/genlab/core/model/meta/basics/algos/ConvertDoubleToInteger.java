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
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

public class ConvertDoubleToInteger extends BasicAlgo {


	public static final InputOutput<Double> INPUT = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"in_double", 
			"double", 
			"the double input"
			);

	
	public static final InputOutput<Integer> OUTPUT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_integer", 
			"integer", 
			"(int)round(INPUT)"
			);

	public ConvertDoubleToInteger() {
		
		super(
				"Double to integer", 
				"converts a double value to a integer value: (int)round(INPUT)", 
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
				
				Double inValue = (Double)getInputValueForInput(INPUT);
				
				Integer result = (int)Math.round(inValue);
				
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
