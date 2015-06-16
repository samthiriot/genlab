package genlab.igraph.algos.measure;

import java.util.Map;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class AbstractIGraphMeasureInstance extends AlgoInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractIGraphMeasureInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
		
	}

	public AbstractIGraphMeasureInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
	}


}
