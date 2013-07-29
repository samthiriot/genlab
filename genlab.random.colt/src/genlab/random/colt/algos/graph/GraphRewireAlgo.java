package genlab.random.colt.algos.graph;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * TODO parameter random
 * TODO param add double
 * TODO avoid loops ?
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphRewireAlgo extends AbstractBasicAlgoRandomEdges {
	
	public static IntegerInOut INPUT_COUNT = new IntegerInOut(
			"in_count", 
			"count", 
			"number of edges to rewire"
			);
			
	public GraphRewireAlgo() {
		super(
				"rewire (colt)", 
				"Randomly rewires a graph while preserving the degree distribution. Avoids loops."
				);
		inputs.add(INPUT_COUNT);
	}

	
	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractAlgoRandomEdgeExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 1000;
			}
			
			@Override
			protected IGenlabGraph computeGraph(IGenlabGraph inGraph,
					RandomEngine randomEngine) {
				
				Integer count = (Integer)getInputValueForInput(INPUT_COUNT);



				IGenlabGraph outGraph = null;
				
				if (count == 0) {
					outGraph = inGraph;
				} else {
					progress.setProgressTotal(count);

					outGraph = inGraph.clone(inGraph.getGraphId()+"_clone");
					
					Uniform coltUniform = new Uniform(
							0, 
							(int)(outGraph.getEdgesCount()-1), 
							randomEngine
							);
					
					int rewired = 0;
					while (rewired < count) {

						String edgeA = outGraph.getEdge(
								coltUniform.nextInt()
								);
						String edgeB = outGraph.getEdge(
								coltUniform.nextInt()
								);
						String edgeA1 = outGraph.getEdgeVertexFrom(edgeA);
						String edgeA2 = outGraph.getEdgeVertexTo(edgeA);
						String edgeB1 = outGraph.getEdgeVertexFrom(edgeB);
						String edgeB2 = outGraph.getEdgeVertexTo(edgeB);
						
						System.err.println("rewire:");
						System.err.println(edgeA);
						System.err.println(edgeB);
						
						// previously:
						// edgeA (a1, a2) and edgeB(b1, b2)
						// to be rewired as: 
						// edgeA (a1, b2) and edgeB(b1, a2)
						
						// avoid loops
						if (edgeA1.equals(edgeB2) || edgeB1.equals(edgeA2)) {
							System.err.println("no: would be a loop");
							continue;
						}
						
						// avoid double edges
						if (outGraph.containsEdge(edgeA1, edgeB2) || outGraph.containsEdge(edgeB1, edgeA2)) {
							System.err.println("no: would be a double link");
							continue;
						}
						
						// save previous parameters
						
						// create new edge 1:
						{
						String edgeNewA = outGraph.addEdge(
								edgeA1, 
								edgeB2, 
								outGraph.isEdgeDirected(edgeA)
								);
						outGraph.setEdgeAttributes(
								edgeNewA,
								outGraph.getEdgeAttributes(edgeA)
								);
						}
						
						// and 2:
						{
						String edgeNewB = outGraph.addEdge(
								edgeB1, 
								edgeA2, 
								outGraph.isEdgeDirected(edgeA)
								);
						outGraph.setEdgeAttributes(
								edgeNewB,
								outGraph.getEdgeAttributes(edgeB)
								);
						}
						
						// and now remove the old edges
						outGraph.removeEdge(edgeA);
						outGraph.removeEdge(edgeB);
						
						
						rewired++;
						progress.incProgressMade();

					}
					
				}
				return outGraph;
			}
		};
	}

}
