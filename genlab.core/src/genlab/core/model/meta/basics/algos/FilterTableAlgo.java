package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

public class FilterTableAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to filter"
			);
	
	public static final InputOutput<IGenlabTable> OUTPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"out_table", 
			"out", 
			"the filtered table"
			);
	
	public FilterTableAlgo(String name, String description,
			String longHtmlDescription, String categoryId, String imagePath) {
		super(
				"filter table", 
				"removes lines from ", 
				null, 
				categoryId, 
				imagePath
				);
		// TODO Auto-generated constructor stub
	}

	public FilterTableAlgo(String name, String description, String categoryId) {
		super(name, description, categoryId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}

}
