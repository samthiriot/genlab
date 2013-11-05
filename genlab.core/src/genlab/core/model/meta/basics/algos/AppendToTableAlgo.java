package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IReduceAlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IReduceAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

public class AppendToTableAlgo extends BasicAlgo implements IReduceAlgo {

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
		
		return new AppendToTableExecutable(execution, algoInstance);
	
	}

}
