package genlab.algog.algos.meta;

import genlab.algog.core.Activator;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;

import org.osgi.framework.Bundle;

public abstract class AbstractGeneticAlgo extends BasicAlgo {

	public AbstractGeneticAlgo(String name, String description) {
		super(
				name, 
				description, 
				null, 
				ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getTotalId(), 
				"/icons/dna.gif"
				);
	}
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}


}
