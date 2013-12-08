package genlab.cytoscape.commons;

import org.osgi.framework.Bundle;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.cytoscape.Activator;

public abstract class CytoscapeAlgo extends BasicAlgo {

	public CytoscapeAlgo(String name, String description, AlgoCategory category) {
		super(
				name, 
				description, 
				category, 
				"/icons/cytoscape_logo.gif", // TODO !!!
				null
				);

	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}


}
