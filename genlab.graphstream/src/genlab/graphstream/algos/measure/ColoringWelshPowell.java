package genlab.graphstream.algos.measure;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.coloring.WelshPowell;
import org.graphstream.graph.Graph;

import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.AbstractGraphstreamBasedGraph;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.StringParameter;
import genlab.core.usermachineinteraction.ListOfMessages;

public class ColoringWelshPowell extends AbstractGraphStreamMeasure {

	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"colored graph"
	);
	
	public static final IntegerInOut OUTPUT_COLORS_COUNT = new IntegerInOut(
			"out_count", 
			"count", 
			"count of colors"
			);
	
	public static final StringParameter PARAM_ATTRIBUTE_NAME = new StringParameter(
			"param_attribute", 
			"attribute name", 
			"vertex attribute to use for color", 
			"color"
			);
	
	public ColoringWelshPowell() {
		super("coloring Welsh-Powell (graphstream)", "Welsh-Powell algorithm for the problem of graph coloring");
		
		outputs.add(OUTPUT_GRAPH);
		outputs.add(OUTPUT_COLORS_COUNT);
		
		registerParameter(PARAM_ATTRIBUTE_NAME);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractGraphstreamMeasureExecution(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 1000;
			}
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, Graph gsGraph,
					IGenlabGraph genlabGraph, ListOfMessages messages) {
				
				Map<IInputOutput<?>, Object> res = new HashMap<IInputOutput<?>, Object>(5);
				
				IGenlabGraph outGraph = genlabGraph.clone("dup");
				String attributeId = (String)algoInst.getValueForParameter(PARAM_ATTRIBUTE_NAME.getId());
				
				if (isUsed(OUTPUT_GRAPH) || exec.getExecutionForced()) {
					
					outGraph.declareVertexAttribute(attributeId, Integer.class);
					
				}
				
				Graph outGSgraph = ((AbstractGraphstreamBasedGraph)outGraph)._getInternalGraphstreamGraph();
				
				WelshPowell wp = new WelshPowell(attributeId);
				wp.init(outGSgraph);
				wp.compute();

				res.put(OUTPUT_COLORS_COUNT, wp.getChromaticNumber());
				res.put(OUTPUT_GRAPH, outGraph);
				
				return res;
			}

		
		};
	}

}
