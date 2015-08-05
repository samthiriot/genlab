package genlab.core.model.meta.basics.algos;

import java.util.ArrayList;
import java.util.Collections;

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
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.ListParameter;

public class StatisticsOfColumnAlgo extends BasicAlgo {


	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"Table", 
			"the table to analyze"
			);
	
	public static final BooleanParameter PARAM_VARIANCE_SAMPLE = new BooleanParameter(
			"param_sample_variance", 
			"use unbiased sample variance", 
			"unbiased sample variance uses the Bessel's correction", 
			Boolean.TRUE
			);
	
	public static final InputOutput<Double> OUTPUT_AVERAGE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_average", 
			"Mean", 
			"Average of the column"
			);
	
//	public static final InputOutput<Double> OUTPUT_VARIANCE = new InputOutput<Double>(
//			DoubleFlowType.SINGLETON, 
//			"out_variance", 
//			"variance", 
//			"variance for this column"
//			);
	
	public static final InputOutput<Double> OUTPUT_STD = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_std", 
			"SD", 
			"Standard deviation for this column"
			);
	
	public static final InputOutput<Double> OUTPUT_MIN = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_min", 
			"Min", 
			"Minimum value for this column"
			);
	
	public static final InputOutput<Double> OUTPUT_Q1 = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_q1", 
			"Q1", 
			"First quartile"
			);
	
	public static final InputOutput<Double> OUTPUT_MEDIAN = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_median", 
			"Median", 
			"Median for this column"
			);
	
	public static final InputOutput<Double> OUTPUT_Q3 = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_q3", 
			"Q3", 
			"Third quartile"
			);
	
	public static final InputOutput<Double> OUTPUT_MAX = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_max", 
			"Max", 
			"Maximum value for this column"
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
		outputs.add(OUTPUT_STD);
		outputs.add(OUTPUT_MIN);
		outputs.add(OUTPUT_Q1);
		outputs.add(OUTPUT_MEDIAN);
		outputs.add(OUTPUT_Q3);
		outputs.add(OUTPUT_MAX);
		
		registerParameter(PARAM_VARIANCE_SAMPLE);
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
				
				final ListParameter paramColumn = ((StatisticsOfColumnInstance)algoInst).PARAM_COLUMN;
				final Integer columnOptionsIdx = (Integer)algoInst.getValueForParameter(paramColumn);
				final String columnId = paramColumn.getLabel(columnOptionsIdx);

				final Boolean useSampleVariance = (Boolean) algoInst.getValueForParameter(PARAM_VARIANCE_SAMPLE);
				
				ComputationResult res = new ComputationResult(algoInst, progress, messages);
				setResult(res);
				
				// specific case: no row !
				if (table.getRowsCount() == 0 || (useSampleVariance && table.getRowsCount() == 1)) {
					messages.warnUser("the table contains not enough rows for computing variance", getClass());
					res.setResult(OUTPUT_AVERAGE, Double.NaN);
					res.setResult(OUTPUT_STD, Double.NaN);
					res.setResult(OUTPUT_MIN, Double.NaN);
					res.setResult(OUTPUT_Q1, Double.NaN);
					res.setResult(OUTPUT_MEDIAN, Double.NaN);
					res.setResult(OUTPUT_Q3, Double.NaN);
					res.setResult(OUTPUT_MAX, Double.NaN);
					progress.setComputationState(ComputationState.FINISHED_OK);
					return;
				}
				
				// else
				try {
					// compute average, min, q1, median, q3 and max
					double average = .0;
					double totalValue = .0;
					int size = table.getRowsCount();
					ArrayList<Double> values = new ArrayList<>(size);
					for( int i=0 ; i<size ; i++ ) {
						final double currentValue = ((Number)table.getValue(i, columnId)).doubleValue();
						values.add(currentValue);
						totalValue += currentValue;
					}
					average = totalValue/size;
					res.setResult(OUTPUT_AVERAGE, average);
					Collections.sort(values);
					res.setResult(OUTPUT_MIN, values.get(0));
					res.setResult(OUTPUT_Q1, values.get((size+1)/4));
					res.setResult(OUTPUT_MEDIAN, values.get((size+1)/2));
					res.setResult(OUTPUT_Q3, values.get(3*(size+1)/4));
					res.setResult(OUTPUT_MAX, values.get(size-1));
					
					double variance = .0;
					// then compute variance
					if( isUsed(OUTPUT_STD) ) {
						totalValue = .0;
						for( int i=0 ; i<size ; i++ ) {
							final double currentValue = ((Number)table.getValue(i, columnId)).doubleValue();
							totalValue += StrictMath.pow(
								average - currentValue,
								2.0
							);
						}
						// Bessel's correction
						if (useSampleVariance) {
							variance = totalValue/(size-1);
						}
						else {
							variance = totalValue/size;
						}
						
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
