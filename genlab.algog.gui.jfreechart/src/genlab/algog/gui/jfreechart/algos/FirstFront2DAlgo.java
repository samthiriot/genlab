package genlab.algog.gui.jfreechart.algos;

import genlab.algog.gui.jfreechart.exec.FirstFront2DExec;
import genlab.algog.gui.jfreechart.instance.FirstFront2DInstance;
import genlab.algog.gui.jfreechart.views.FirstFront2DView;
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
import genlab.gui.jfreechart.algos.AbstractJFreechartAlgo;

public class FirstFront2DAlgo extends AbstractJFreechartAlgo {


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
	
	public FirstFront2DAlgo() {
		super(
				"Pareto front plot", 
				"displays the data as a Pareto front", 
				FirstFront2DView.VIEW_ID,
				ExistingAlgoCategories.DISPLAY_EXPLORATION_GENETIC_ALGOS
				);
		
		inputs.add(INPUT_TABLE);
		registerParameter(PARAM_COLUMN_X);
		registerParameter(PARAM_COLUMN_Y);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new FirstFront2DExec(execution, algoInstance);
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new FirstFront2DInstance(this, workflow);
	}

	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new FirstFront2DInstance(this, workflow, id);
	}

	
}
