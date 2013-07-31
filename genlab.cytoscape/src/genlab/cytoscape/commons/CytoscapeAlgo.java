package genlab.cytoscape.commons;

import org.osgi.framework.Bundle;

import genlab.core.model.meta.BasicAlgo;
import genlab.cytoscape.Activator;

public abstract class CytoscapeAlgo extends BasicAlgo {

	public CytoscapeAlgo(String name, String description, String categoryId) {
		super(
				name, description, 
				null, 
				categoryId, 
				"/icons/cytoscape_logo.gif"
				);

	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}


}
