package genlab.graphstream.ui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.graphstream.algos.GraphStreamAlgo;
import genlab.graphstream.ui.views.GraphView;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

/**
 * The algorithm that takes a graph as input
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphDisplayAlgo extends AbstractGraphDisplayAlgo {

	public GraphDisplayAlgo() {
		super(
				"graph display (graphstream)", 
				"displays a graph on an abstract space" 
				);
		
	}
	
	protected String getViewId() {
		return GraphView.VIEW_ID;
	}

	
}
