package genlab.graphstream.algos;

import org.osgi.framework.Bundle;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.graphstream.Activator;


public abstract class GraphStreamAlgo extends BasicAlgo {

	public GraphStreamAlgo(String name, String description, AlgoCategory category) {
		super(
				name, 
				description, 
				category, 
				"/icons/graphstream"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/graphstreamBig.png"
				);

	}
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
