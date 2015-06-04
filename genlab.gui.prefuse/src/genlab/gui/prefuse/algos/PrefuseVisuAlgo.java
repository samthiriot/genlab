package genlab.gui.prefuse.algos;

import java.util.LinkedList;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.ListParameter;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.prefuse.views.PrefuseVisuView;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public class PrefuseVisuAlgo extends BasicAlgo {


	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public static final ListParameter PARAM_ATTRIBUTE_VERTEX_COLORING = new ListParameter(
			"param_vertex_coloring", 
			"color vertices for", 
			"the vertex attribute to color the vertex for",
			0,
			new LinkedList<String>() 
			);
	
	public static final IntParameter PARAM_LOW_QUALITY_THRESHOLD = new IntParameter(
			"param_low_quality_threshold", 
			"low quality threshold", 
			"switch to low quality above the following count of edges+nodes", 
			2000, 
			100,
			10000,
			100
			);
	
	public PrefuseVisuAlgo() {
		super(
				"spring view", 
				"graph vizualisation with a spring layout and colors", 
				ExistingAlgoCategories.DISPLAY_GRAPH, 
				null, 
				null
				);

		inputs.add(INPUT_GRAPH);
		
		registerParameter(PARAM_LOW_QUALITY_THRESHOLD);
		registerParameter(PARAM_ATTRIBUTE_VERTEX_COLORING);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractOpenViewAlgoExec(execution, algoInstance, PrefuseVisuView.VIEW_ID) {
			
			IGenlabGraph glGraph = null;

			@Override
			protected void displayResultsSync(AbstractViewOpenedByAlgo theView) {
								
				GLLogger.traceTech("displaying the graph...", getClass());
				PrefuseVisuView gv = (PrefuseVisuView)theView;
				
				theView.receiveData(glGraph);
			}

			@Override
			public long getTimeout() {
				return 1000*5;
			}

			@Override
			protected void loadDataSuccessiveFromInput() {
				
				glGraph = (IGenlabGraph)getInputValueForInput(INPUT_GRAPH);

			}

			@Override
			protected void displayResultsSyncReduced(
					AbstractViewOpenedByAlgo theView,
					IAlgoExecution executionRun,
					IConnectionExecution connectionExec, Object value) {
				// TODO Auto-generated method stub
				
				// TODO implement the display reduced for graphstream ?
			}
		};
	}
	

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// add a bonus, as it is really great
		return super.getPriorityForIntuitiveCreation()+30;
	}
	

}
