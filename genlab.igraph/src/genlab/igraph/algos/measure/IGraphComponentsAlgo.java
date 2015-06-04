package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

/**
 * Groups everything related to components in the igraph library
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphComponentsAlgo extends AbstractIGraphMeasure {


	public static final InputOutput<Boolean> OUTPUT_ISCONNECTED = new InputOutput<Boolean>(
			BooleanFlowType.SINGLETON, 
			"out_isConnected", 
			"is connected", 
			"returns true if the graph is strongly connected"
	);
	

	public static final InputOutput<Integer> OUTPUT_SIZE_GIANT_CLUSTER = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_giantclustersize", 
			"giant cluster", 
			"size of the biggest (giant) cluster"
	);
	
	public IGraphComponentsAlgo() {
		super(
				"components (igraph)", 
				"components processing igraph implementation",
				null // no preference for implementation
				);
		outputs.add(OUTPUT_ISCONNECTED);
		outputs.add(OUTPUT_SIZE_GIANT_CLUSTER);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new IGraphComponentsExec(execution, algoInstance);
	}

}
