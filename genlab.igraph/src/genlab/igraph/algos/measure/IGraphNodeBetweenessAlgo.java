package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * Groups everything related to components in the igraph library
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphNodeBetweenessAlgo extends AbstractIGraphMeasure {

	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph with betweeness"
	);
	
// TODO category centrality measures
	
	public IGraphNodeBetweenessAlgo() {
		super(
				"node betweeness (igraph)", 
				"measure node betweeness centrality using the igraph implementation",
				null // TODO change that
				);
		outputs.add(OUTPUT_GRAPH);
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
				
				IGraphLibrary lib = new IGraphLibrary();
				
				// TODO actual parameter
				final String parameterAttribute  = "nodeBetweenessIgraph";
				final double cutoff = 0;
				
				// is connected
				if (isUsed(OUTPUT_GRAPH)) {
					
					double[] nodeBetweennes = lib.computeBetweenessEstimate(igraphGraph, false, cutoff);
					
					IGenlabGraph output = genlabGraph.clone("cloned");
					
					// declare the resulting parameter
					output.declareVertexAttribute(parameterAttribute, Double.class);
					
					// TODO same index for igraph and other graph ???
					for (int i=0; i<nodeBetweennes.length; i++) {
						
						String vertexId = output.getVertex(i);
						// TODO add a method to allow a graph to change all the values ?
						output.setVertexAttribute(
								vertexId, 
								parameterAttribute, 
								nodeBetweennes[i]
								);
						
					}
					
					results.put(OUTPUT_GRAPH, output);
					
				} else {
					messages.debugTech("the average path length is not used, so it will not be computed", getClass());	
				}
				

				
				return results;
			}

			@Override
			public long getTimeout() {
				// TODO interesting timeout given the complexity
				return 1000*60*5;
			}

		
		};
	}

}
