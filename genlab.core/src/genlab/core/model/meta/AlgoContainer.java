package genlab.core.model.meta;

import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;

/**
 * An algo container, that is a container of algos (like a loop for instance)
 * 
 * @author Samuel Thiriot
 */
public abstract class AlgoContainer extends BasicAlgo implements IAlgoContainer {

	public AlgoContainer(
			String name, 
			String description,
			AlgoCategory category, 
			String imagePath,
			String imagePathBig
			) {
		super(name, description, category, imagePath, imagePathBig);
	}

	public AlgoContainer(String name, String description, AlgoCategory category) {
		super(name, description, category, null, null);
	}


	@Override
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new AlgoContainerInstance(this, workflow, id); // TODO id ??? 
	}


	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new AlgoContainerInstance(this, workflow);
	}
	
	@Override
	public boolean canContain(IAlgo algo) {
		// by default, returns true. Override if relevant.
		return true;
	}



}
