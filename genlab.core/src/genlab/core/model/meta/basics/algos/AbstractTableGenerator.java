package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

/**
 * Abstract class for any algo which intends to create a table from nothing (random, empty, filled...)
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractTableGenerator extends BasicAlgo {


	public static final InputOutput<Integer> INPUT_ROWS = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"nbRows", 
			"rows count", 
			"the number of rows to create"
			);
	
	public static final InputOutput<Integer> INPUT_COLS = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"nbCols", 
			"columns count", 
			"the number of columns to create"
			);
	
	
	public static final InputOutput<IGenlabTable> OUTPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"table", 
			"table", 
			"a random table filled as specified"
			);
	
	
	public AbstractTableGenerator(String name, String desc) {
		
		super(
				name, 
				desc, 
				null, 
				ExistingAlgoCategories.GENERATORS.getTotalId(), 
				null
				);
		
		inputs.add(INPUT_COLS);
		inputs.add(INPUT_ROWS);
		
		outputs.add(OUTPUT_TABLE);
		
	}

	

}
