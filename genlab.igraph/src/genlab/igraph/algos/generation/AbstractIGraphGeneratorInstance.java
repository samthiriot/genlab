package genlab.igraph.algos.generation;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;

@SuppressWarnings("serial")
public class AbstractIGraphGeneratorInstance extends AlgoInstance {

	public AbstractIGraphGeneratorInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public AbstractIGraphGeneratorInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	public Object getPrecomputedValueForOutput(IInputOutput<?> output) {
		
		if (output == AbstractIGraphGenerator.OUTPUT_GRAPH) {
		
			return GraphFactory.createGraph(
					"precomputed",
					// TODO define directionnality based on parameters ?
					GraphDirectionality.MIXED,
					// TODO ?
					false
					);
			
		} else {
			
			return super.getPrecomputedValueForOutput(output);
		}
	}

	
	
	
}
