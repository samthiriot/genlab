package genlab.core.model.instance;

import genlab.core.model.meta.IAlgo;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Instance of an algo container, that is an algo which contains
 * other algos (like loops for instance)
 * 
 * @author Samuel Thiriot
 *
 */
public class AlgoContainerInstance extends AlgoInstance implements IAlgoContainerInstance {

	private transient Collection<IAlgoInstance> children = new LinkedList<IAlgoInstance>();
	
	public AlgoContainerInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	public Collection<IAlgoInstance> getChildren() {
		return children;
	}

	@Override
	public void addChildren(IAlgoInstance child) {
		if (!children.contains(child))
			children.add(child);
	}

	@Override
	public void removeChildren(IAlgoInstance child) {
		children.remove(child);
	}

	
	
}
