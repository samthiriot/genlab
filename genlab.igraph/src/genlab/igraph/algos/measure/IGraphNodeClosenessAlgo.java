package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.StringParameter;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

/**
 * Groups everything related to components in the igraph library
 * 
 * TODO warning this is a edge betweeness 
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphNodeClosenessAlgo extends AbstractIGraphMeasure {


	public static final StringParameter PARAM_ATTRIBUTE_NAME = new StringParameter(
			"attribute_name", 
			"attribute name", 
			"the name of the attribute of vertices which will store the value", 
			"igraph_node_closeness"
			); 
	
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph with closeness"
	);
	
// TODO category centrality measures
	
	public IGraphNodeClosenessAlgo() {
		super(
				"node closeness (igraph)", 
				"measure node closeness centrality using the igraph implementation",
				EIgraphImplementation.R_ONLY // we accept only R for this operation
				);

		outputs.add(OUTPUT_GRAPH);
		
		registerParameter(PARAM_ATTRIBUTE_NAME);

	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new IGraphNodeClosenessExec(execution, algoInstance, algoInstance);
	}

}
