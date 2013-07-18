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
public class GraphDisplayAlgo extends GraphStreamAlgo {


	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public GraphDisplayAlgo() {
		super(
				"graph display (graphstream)", 
				"displays a graph on an abstract space", 
				ExistingAlgoCategories.DISPLAY.getTotalId()
				);
		
		inputs.add(INPUT_GRAPH);

	}

	@Override
	public IAlgoExecution createExec(
			IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractOpenViewAlgoExec(execution, algoInstance, GraphView.VIEW_ID) {
			
			@Override
			protected void displayResults(AbstractViewOpenedByAlgo theView) {
				
				GLLogger.traceTech("displaying the graph...", getClass());
				GraphView gv = (GraphView)theView;
				
				IGenlabGraph glGraph = (IGenlabGraph)getInputValueForInput(INPUT_GRAPH);
				
				gv.displayGraph(glGraph);
			}
		};
	}

	
}
