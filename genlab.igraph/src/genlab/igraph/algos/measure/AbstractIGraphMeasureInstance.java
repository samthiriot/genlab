package genlab.igraph.algos.measure;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;

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
