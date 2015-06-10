package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

public class StatisticsOfColumnAlgo extends BasicAlgo {


	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to analyze"
			);
	
	public static final InputOutput<Double> OUTPUT_AVERAGE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_mean", 
			"mean", 
			"contains the mean of the column"
			);
	

	public static final InputOutput<Double> OUTPUT_VARIANCE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_variance", 
			"variance", 
			"variance for this column"
			);
	
	public static final InputOutput<Double> OUTPUT_STD = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_std", 
			"standard deviation", 
			"standard deviation for this column"
			);
	
	public StatisticsOfColumnAlgo() {
		super(
				"statistics of a column", 
				"computes the mean, variance and  value of a column of a table", 
				ExistingAlgoCategories.ANALYSIS_TABLE, 
				null, 
				null
				);

		inputs.add(INPUT_TABLE);
		outputs.add(OUTPUT_AVERAGE);
		outputs.add(OUTPUT_VARIANCE);
		outputs.add(OUTPUT_STD);
	}

	
	
	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new StatisticsOfColumnInstance(this, workflow);
	}



	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new StatisticsOfColumnInstance(this, workflow, id);
	}



	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		
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
				
				// retrieve inputs
				final IGenlabTable table = (IGenlabTable)getInputValueForInput(INPUT_TABLE);
				
				
				final Integer columnOptionsIdx = (Integer)algoInst.getValueForParameter(StatisticsOfColumnInstance.PARAM_COLUMN);
				final String columnId = StatisticsOfColumnInstance.PARAM_COLUMN.getLabel(columnOptionsIdx);

				
				final boolean computeSTD = isUsed(OUTPUT_STD);
				final boolean computeVariance = computeSTD || isUsed(OUTPUT_VARIANCE);
				final boolean computeAverage = computeSTD || isUsed(OUTPUT_AVERAGE);
				
				ComputationResult res = new ComputationResult(algoInst, progress, messages);
				setResult(res);
				
				// specific case: no row !
				if (table.getRowsCount() == 0) {
					messages.warnUser("the table contains no row", getClass());
					res.setResult(OUTPUT_AVERAGE, Double.NaN);
					res.setResult(OUTPUT_STD, Double.NaN);
					progress.setComputationState(ComputationState.FINISHED_OK);
					return;
				}
				
				// else compute the average
				try {
					double average = .0;
					if (computeAverage) {
						double totalValue = .0;
						for (int i=0; i<table.getRowsCount(); i++) {
							totalValue += ((Number)table.getValue(i, columnId)).doubleValue();
						}
						average = totalValue/table.getRowsCount();
						res.setResult(OUTPUT_AVERAGE, average);
					}
					double variance = .0;
					if (computeVariance) {
						double totalValue = .0;
						for (int i=0; i<table.getRowsCount(); i++) {
							totalValue += StrictMath.pow(
									average - ((Number)table.getValue(i, columnId)).doubleValue(),
									2.0
									);
						}
						variance = totalValue/table.getRowsCount();
						res.setResult(OUTPUT_VARIANCE, variance);
					}
					if (computeSTD) {
						res.setResult(OUTPUT_STD, StrictMath.sqrt(variance));
					}
					progress.setComputationState(ComputationState.FINISHED_OK);
				} catch (ClassCastException e) {
					messages.errorUser("the column "+columnId+" does not contain numerics", getClass());
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
				} catch (RuntimeException e) {
					messages.errorUser("error while computing the average of the column: "+e.getMessage(), getClass(), e);progress.setComputationState(ComputationState.FINISHED_FAILURE);
				}
				
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

}
