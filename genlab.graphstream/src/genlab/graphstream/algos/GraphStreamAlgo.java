package genlab.graphstream.algos;

import org.osgi.framework.Bundle;

import genlab.core.model.meta.BasicAlgo;
import genlab.graphstream.Activator;


public abstract class GraphStreamAlgo extends BasicAlgo {

	public GraphStreamAlgo(String name, String description, String categoryId) {
		super(name, description, null, categoryId, "/icons/graphstream.gif");

	}
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
