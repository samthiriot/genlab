package genlab.core.model.meta.basics.algos;

import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

public class AppendToTableAlgo extends BasicAlgo {

	public static final InputOutput<Object> INPUT_ANYTHING = new InputOutput<Object>(
			AnythingFlowType.SINGLETON, 
			"in_anything", 
			"anything", 
			"any input that can be stored into a cell of a table",
			true
			);
	
	public static final InputOutput<IGenlabTable> OUTPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"out_table", 
			"table", 
			"a table with all the values"
			);
	
	public AppendToTableAlgo() {
		super(
				"append table", 
				"appends any value provided in input to the table", 
				null, 
				ExistingAlgoCategories.CASTING.getTotalId(), 
				null
				);
		
		inputs.add(INPUT_ANYTHING);
		outputs.add(OUTPUT_TABLE);
	}

	@Override
	public IAlgoExecution createExec(final IExecution execution,
			final AlgoInstance algoInstance) {
		return new AbstractAlgoExecution(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void run() {
				
				progress.setComputationState(ComputationState.STARTED);
				
				Map<IConnection,Object> inputs = getInputValuesForInput(INPUT_ANYTHING);
				progress.setProgressTotal(inputs.size());
				
				// TODO how to deal with no creation ? 
				IGenlabTable outputTable = new GenlabTable();
				final int rowId = outputTable.addRow();
				
				for (IConnection c: inputs.keySet()) {
					final Object value = inputs.get(c);

					final String columnId = c.getFrom().getAlgoInstance().getName()+"/"+c.getFrom().getMeta().getName();
					if (!outputTable.containsColumn(columnId))
						outputTable.declareColumn(columnId);
					
					outputTable.setValue(rowId, columnId, value);
					
					progress.incProgressMade();
				}
				
				ComputationResult result = new ComputationResult(algoInstance, progress, execution.getListOfMessages());
				
				setResult(result);
				result.setResult(OUTPUT_TABLE, outputTable);
				
				progress.setComputationState(ComputationState.FINISHED_OK);
				
			}
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 1000;
			}
		};
	}

}
