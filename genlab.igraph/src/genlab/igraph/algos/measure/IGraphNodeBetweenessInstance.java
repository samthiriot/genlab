package genlab.igraph.algos.measure;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

@SuppressWarnings("serial")
public class IGraphNodeBetweenessInstance extends AbstractIGraphMeasureInstance {

	public IGraphNodeBetweenessInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public IGraphNodeBetweenessInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}
	
	/**
	 * Default behaviour is to return nothing.
	 * Please override to return something meaningfull when possible.
	 */
	@Override
	public Object getPrecomputedValueForOutput(IInputOutput<?> output) {

		final IGenlabGraph glGraph = (IGenlabGraph)getPrecomputedValueForInput(IGraphNodeBetweenessAlgo.INPUT_GRAPH);
		if (glGraph == null)
			return null;
		
		final String parameterAttribute = (String)getValueForParameter(IGraphNodeBetweenessAlgo.PARAM_ATTRIBUTE_NAME);

		glGraph.declareVertexAttribute(parameterAttribute, Double.class);
		
		return glGraph;
	}


}
