package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Groups everything related to components in the igraph library
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphComponentsAlgo extends AbstractIGraphMeasure {


	public static final InputOutput<Boolean> OUTPUT_ISCONNECTED = new InputOutput<Boolean>(
			new BooleanFlowType(), 
			"TODO.isConnected", 
			"is connected", 
			"connectivity stored by the igraph library" +
			"TODO"
	);
	

	public static final InputOutput<Integer> OUTPUT_SIZE_GIANT_CLUSTER = new InputOutput<Integer>(
			new IntegerFlowType(), 
			"TODO.giant cluster size", 
			"giant cluster", 
			"size of the biggest (giant) cluster" +
			"TODO"
	);
	
	public IGraphComponentsAlgo() {
		super(
				"components", 
				"components processing igraph implementation"
				);
		outputs.add(OUTPUT_ISCONNECTED);
		outputs.add(OUTPUT_SIZE_GIANT_CLUSTER);
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
				
				// is connected
				if (isUsed(OUTPUT_ISCONNECTED)) {
					boolean isConnected = igraphGraph.lib.isConnected(igraphGraph);
					results.put(OUTPUT_ISCONNECTED, isConnected);
				} else {
					messages.debugTech("the average path length is not used, so it will not be computed", getClass());	
				}
				

				// average path length
				if (isUsed(OUTPUT_SIZE_GIANT_CLUSTER)) {
					int giantClusterSize = igraphGraph.lib.computeGiantCluster(igraphGraph);
					results.put(OUTPUT_SIZE_GIANT_CLUSTER, giantClusterSize);
				} else {
					messages.debugTech("the size of the giant cluster is not used, so it will not be computed", getClass());	
				}
				
				return results;
			}
		};
	}

}
