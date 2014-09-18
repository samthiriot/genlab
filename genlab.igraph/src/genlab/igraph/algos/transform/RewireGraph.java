package genlab.igraph.algos.transform;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

public class RewireGraph extends AbstractIGraphTransform {

	public static IntegerInOut INPUT_COUNT = new IntegerInOut(
			"in_count", 
			"count", 
			"number of edges to rewire",
			10
			);
	
	public RewireGraph() {
		super(
				"rewire (igraph)", 
				"This function generates a new graph based on the original one by randomly rewiring edges while preserving the original graph's degree distribution.", 
				ExistingAlgoCategories.NOISE_GRAPH
				);
		
		inputs.add(INPUT_COUNT);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractIGraphTransformExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 500;
			}
			
			@Override
			protected void transformGraph(IComputationProgress progress,
					IGraphGraph igraphGraph, IGenlabGraph genlabGraph,
					ListOfMessages messages) {
				
				Integer count = (Integer)getInputValueForInput(INPUT_COUNT);
						
				igraphGraph.lib.rewire(igraphGraph, count);
			}
		};
	}

}
