package genlab.gui.jfreechart.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;
import genlab.core.parameters.ListParameter;
import genlab.gui.jfreechart.exec.ScatterPlotExec;
import genlab.gui.jfreechart.instance.ScatterPlotAlgoInstance;
import genlab.gui.jfreechart.views.ScatterView;

public class ScatterPlotAlgo extends AbstractJFreechartAlgo {


	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to be displayed as a chart display"
			);
	
	public static final ListParameter PARAM_COLUMN_X = new ListParameter(
			"param_columnX", 
			"column for X", 
			"the column of the table to use as a X value"
			);
	
	public static final ListParameter PARAM_COLUMN_Y = new ListParameter(
			"param_columnY", 
			"column for Y", 
			"the column of the table to use as a y value"
			);
	
	public ScatterPlotAlgo() {
		super(
				"scatter plot", 
				"displays scatter data", 
				ScatterView.VIEW_ID,
				ExistingAlgoCategories.DISPLAY_TABLES
				);
		
		inputs.add(INPUT_TABLE);
		registerParameter(PARAM_COLUMN_X);
		registerParameter(PARAM_COLUMN_Y);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ScatterPlotExec(execution, algoInstance);
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new ScatterPlotAlgoInstance(this, workflow);
	}

	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new ScatterPlotAlgoInstance(this, workflow, id);
	}

	
}
