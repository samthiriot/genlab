package genlab.igraph.algos;

import org.osgi.framework.Bundle;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.igraph.Activator;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.parameters.ChoiceOfImplementationParameter;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public abstract class AbstractIGraphAlgo extends BasicAlgo {

	public static final ChoiceOfImplementationParameter PARAM_IMPLEMENTATION = new ChoiceOfImplementationParameter();

	protected final EIgraphImplementation implementationAcceptedOnly;

	public AbstractIGraphAlgo(
			String name, 
			String description,
			AlgoCategory category,
			EIgraphImplementation implementationAcceptedOnly
			) {
		super(name, description, category, 
				"/icons/igraph"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/igraphBig.png");

		registerParameter(PARAM_IMPLEMENTATION);
		
		this.implementationAcceptedOnly = implementationAcceptedOnly;
	}


	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	public boolean isAvailable() {
		return IgraphLibFactory.isIGraphAvailable();
	}

}
