package genlab.core.model.meta.basics.algos;

import java.util.Collection;
import java.util.LinkedList;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.IReduceAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
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
				ExistingAlgoCategories.CASTING, 
				null, 
				null
				);
		
		inputs.add(INPUT_ANYTHING);
		

		OUTPUT_TABLE.setIsContinuousOutput(true);
		
		outputs.add(OUTPUT_TABLE);
	}
	
	

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new AppendToTableInstance(this, workflow);
	}



	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new AppendToTableInstance(this, workflow, id);
		}



	@Override
	public IAlgoExecution createExec(final IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new AppendToTableExecutable(execution, algoInstance);
	
	}

}
