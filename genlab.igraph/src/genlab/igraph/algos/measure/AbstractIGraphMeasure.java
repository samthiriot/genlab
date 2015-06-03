package genlab.igraph.algos.measure;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Activator;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.natjna.IGraphRawLibrary;
import genlab.igraph.parameters.ChoiceOfImplementationParameter;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

import org.osgi.framework.Bundle;

public abstract class AbstractIGraphMeasure extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public static final ChoiceOfImplementationParameter PARAM_IMPLEMENTATION = new ChoiceOfImplementationParameter();

	protected final EIgraphImplementation implementationAcceptedOnly;
	
	public AbstractIGraphMeasure(
			String name, 
			String description,
			EIgraphImplementation implementationAcceptedOnly
			) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.ANALYSIS_GRAPH,
				"/icons/igraph"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/igraphBig.png"
				);
		
		this.implementationAcceptedOnly = implementationAcceptedOnly;
		
		inputs.add(INPUT_GRAPH);
		
		registerParameter(PARAM_IMPLEMENTATION);
	}
	
	public AbstractIGraphMeasure(
			String name, 
			String description,
			String categoryId,
			EIgraphImplementation implementationAcceptedOnly
			) {
		super(
				name, 
				description, 
				null,
				categoryId,
				"/icons/igraph.gif"
				);
		
		this.implementationAcceptedOnly = implementationAcceptedOnly;
		
		inputs.add(INPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	
	@Override
	public boolean isAvailable() {
		return IGraphRawLibrary.isAvailable;
	}
}
