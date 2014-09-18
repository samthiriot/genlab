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
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphRemoveRandomEdges extends AbstractBasicAlgoRandomEdges {
	
	public static IntegerInOut INPUT_COUNT = new IntegerInOut(
			"in_count", 
			"count", 
			"number of edges to remove",
			10
			);
			
	public GraphRemoveRandomEdges() {
		super(
				"remove random edges (colt)", 
				"remvoe some random edges"
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
				
				// TODO check input parameter
				
				Integer count = (Integer)getInputValueForInput(INPUT_COUNT);


				progress.setProgressTotal(count);

				IGenlabGraph outGraph = null;
				
				if (count == 0) {
					outGraph = inGraph;
				} else {
					// TODO reuse the input graph ?
					outGraph = inGraph.clone(inGraph.getGraphId()+"_clone");
					
					Uniform coltUniform = new Uniform(randomEngine);
					
					
					for (int i=0; i<count; i++) {
						
						outGraph.removeEdge(
								coltUniform.nextIntFromTo(
										0, 
										(int)(outGraph.getEdgesCount()-1)
										)
										);
						progress.incProgressMade();

					}
					
				}
				return outGraph;
			}
		};
	}

}
