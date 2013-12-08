package genlab.gui.jfreechart.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;
import genlab.core.parameters.StringParameter;

public class RadarChartDisplay extends AbstractJFreechartAlgo {
	
	
	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to be displayed as a chart display"
			);
	
	public static final StringParameter PARAM_COLUMN_NAME = new StringParameter(
			"param_column_name", 
			"column for name", 
			"the column containing what will be displayed as a name", 
			"name"
			);
	
	public RadarChartDisplay() {
		super(
				"radar chart", 
				"displays a radar chard into the genlab user interface"
				);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return new ;
	}

}
