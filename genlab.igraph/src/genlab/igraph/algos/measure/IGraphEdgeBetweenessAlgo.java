package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.StringParameter;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.GenlabProgressCallback;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphNativeLibrary;
import genlab.igraph.natjna.IGraphRawLibrary;
import genlab.igraph.natjna.IIGraphProgressCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Groups everything related to components in the igraph library
 * 
 * TODO warning this is a edge betweeness 
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphEdgeBetweenessAlgo extends AbstractIGraphMeasure {


	public static final StringParameter PARAM_ATTRIBUTE_NAME = new StringParameter(
			"attribute_name", 
			"attribute name", 
			"the name of the attribute of edge which will store the value", 
			"igraph_edge_betweeness"
			); 
	
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph with betweeness"
	);
	
// TODO category centrality measures
	
	public IGraphEdgeBetweenessAlgo() {
		super(
				"edge betweeness (igraph)", 
				"measure edge betweeness centrality using the igraph implementation"
				);

		outputs.add(OUTPUT_GRAPH);
		
		registerParameter(PARAM_ATTRIBUTE_NAME);

	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractIGraphMeasureExec(execution, algoInstance) {
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, 
					IGraphGraph igraphGraph,
					IGenlabGraph genlabGraph,
					ListOfMessages messages
					) {
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				IGraphNativeLibrary lib = new IGraphNativeLibrary();

				try {
					
					final String parameterAttribute  = (String) algoInst.getValueForParameter(PARAM_ATTRIBUTE_NAME);
					
					// is connected
					if (isUsed(OUTPUT_GRAPH)) {
						
						double[] nodeBetweennes = lib.computeEdgeBetweeness(igraphGraph, false);
						
						IGenlabGraph output = genlabGraph.clone("cloned");
				/*
						IGraph2GenLabConvertor.addAttributesToNodesGenlabGraphFromIgraph(
								genlabGraph,
								igraphGraph,
								parameterAttribute,
								nodeBetweennes
								);
					*/	
	
						IGraph2GenLabConvertor.addAttributesToEdgesGenlabGraphFromIgraph(
								output,
								igraphGraph,
								parameterAttribute,
								nodeBetweennes
								);
						
						
						results.put(OUTPUT_GRAPH, output);
						
					} else {
						messages.infoUser("the betwenness of the graph is not used, so it will not be computed", getClass());	
					}
					
					return results;
					
				} finally {
				
				}
			}

			@Override
			public long getTimeout() {
				// TODO interesting timeout given the complexity
				return 1000*60*5;
			}

		
		};
	}

}
