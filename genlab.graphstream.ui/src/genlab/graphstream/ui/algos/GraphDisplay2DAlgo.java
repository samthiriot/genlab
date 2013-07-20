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
import genlab.graphstream.ui.views.GraphView2D;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

/**
 * The algorithm that takes a graph as input
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphDisplay2DAlgo extends AbstractGraphDisplayAlgo {


	
	public GraphDisplay2DAlgo() {
		super(
				"2D graph display (graphstream)", 
				"displays a graph in a two dimensionnal space" 
				);
		
	}

	@Override
	protected String getViewId() {
		return GraphView2D.VIEW_ID;
	}


	
}
