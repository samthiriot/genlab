package genlab.random.colt.algos.graph;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
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
public class GraphAddRandomEdges extends AbstractBasicAlgoRandomEdges {
	
	public static IntegerInOut INPUT_COUNT = new IntegerInOut(
			"in_count", 
			"count", 
			"number of edges to add",
			10
			);
			
	public GraphAddRandomEdges() {
		super(
				"add random edges (colt)", 
				"add some random edges"
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


				progress.setProgressTotal(count);

				IGenlabGraph outGraph = null;
				
				if (count == 0) {
					outGraph = inGraph;
				} else {
					outGraph = inGraph.clone(inGraph.getGraphId()+"_clone");
					
					Uniform coltUniform = new Uniform(0, inGraph.getVerticesCount()-1, randomEngine);;
					
					for (int i=0; i<count; i++) {
						
						loopCreateEdge: while (true) {
							String idFrom = outGraph.getVertex(coltUniform.nextInt());
							String idTo = outGraph.getVertex(coltUniform.nextInt());
							if (!outGraph.containsEdge(idFrom, idTo)) {
								outGraph.addEdge(
										idFrom, 
										idTo, 
										outGraph.getDirectionality()==GraphDirectionality.DIRECTED
										);
								break loopCreateEdge;
							}
						}
					
						progress.incProgressMade();

					}
					
				}
				return outGraph;
			}
		};
	}

}
