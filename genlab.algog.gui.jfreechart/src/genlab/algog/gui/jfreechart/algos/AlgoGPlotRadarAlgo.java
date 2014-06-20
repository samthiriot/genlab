package genlab.algog.gui.jfreechart.algos;

import genlab.algog.gui.jfreechart.exec.ViewAlgoGRadarTableExec;
import genlab.algog.gui.jfreechart.exec.ViewAlgoGTableExec;
import genlab.algog.gui.jfreechart.views.ViewAlgogRadarTable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;
import genlab.gui.jfreechart.algos.AbstractJFreechartAlgo;

public class AlgoGPlotRadarAlgo extends AbstractJFreechartAlgo {

	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to be displayed as a chart display"
			);
	
	public AlgoGPlotRadarAlgo() {
		super(
				"algog radar plot", 
				"displays scatter data", 
				ViewAlgogRadarTable.VIEW_ID,
				ExistingAlgoCategories.DISPLAY_EXPLORATION_GENETIC_ALGOS
				);
		
		inputs.add(INPUT_TABLE);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ViewAlgoGRadarTableExec(execution, algoInstance);
	}


	
}
