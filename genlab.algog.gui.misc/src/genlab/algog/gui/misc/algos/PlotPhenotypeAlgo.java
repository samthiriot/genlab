package genlab.algog.gui.misc.algos;

import genlab.algog.gui.misc.algos.views.PlotPhenotypeView;
import genlab.algog.gui.misc.exec.PlotPhenotypeExec;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;


public class PlotPhenotypeAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to be displayed as a chart display"
			);
	
	
	public PlotPhenotypeAlgo() {
		super(
				"phenotype plot", 
				"displays phenotype evolution", 
				ExistingAlgoCategories.DISPLAY_EXPLORATION_GENETIC_ALGOS,
				null,
				null
				);
		
		inputs.add(INPUT_TABLE);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new PlotPhenotypeExec(execution, algoInstance, PlotPhenotypeView.VIEW_ID);
	}


	
}
