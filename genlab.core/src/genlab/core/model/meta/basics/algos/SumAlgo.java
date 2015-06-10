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
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

public class SumAlgo extends BasicAlgo {

	public static final DoubleInOut INPUT_VALUES = new DoubleInOut(
			"in_inputs", 
			"numbers", 
			"all the numbers to sum",
			true
			);
	
	public static final DoubleInOut OUTPUT_TOTAL = new DoubleInOut(
			"out_total", 
			"sum", 
			"the sum of all inputs"
			);
	
	
	public SumAlgo() {
		super(
				"sum numbers", 
				"sums the input numbers", 
				ExistingAlgoCategories.ANALYSIS, 
				null, 
				null
				);

		inputs.add(INPUT_VALUES);
		outputs.add(OUTPUT_TOTAL);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {

		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void cancel() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);	
			}
			
			@Override
			public void run() {
				progress.setProgressTotal(2);
				progress.setComputationState(ComputationState.STARTED);
				
				// retrieve all inputs
				double total = .0;
				
				for (Object v : getInputValuesForInput(INPUT_VALUES).values()) {
					total += ((Number)v).doubleValue();
				}
				
				ComputationResult res = new ComputationResult(algoInst, progress, messages);
				setResult(res);
				res.setResult(OUTPUT_TOTAL, total);
				progress.setComputationState(ComputationState.FINISHED_OK);
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

}
