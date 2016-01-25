package genlab.core.model.instance;

import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.model.meta.IReduceAlgo;

import java.util.Collection;
import java.util.HashSet;
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
	
	public AlgoContainerInstance(IAlgoContainer algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
		
	}

	public AlgoContainerInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
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

	@Override
	public void delete() {
		for (IAlgoInstance ai : children) {
			ai.setContainer(null);
			// TODO delete it ? 
			ai.delete();
		}
		super.delete();
	}

	private Object readResolve() {
		// when unmarshalled (persistence !), we recreate transient objects
		children = new LinkedList<IAlgoInstance>();
		
		return this;
	}
	
	/**
	 * Lists all the algo instances which depend on the results of this algo instance
	 * @return
	 */
	@Override
	public Collection<IAlgoInstance> getAlgoInstancesDependingToOurChildren() {
		
		final Collection<IAlgoInstance> res = new HashSet<IAlgoInstance>();
		
		for (IAlgoInstance child : children) {
		
			for (IInputOutputInstance output : child.getOutputInstances()) {
				
				for (IConnection c: output.getConnections()) {
					
					IAlgoInstance instanceTo = c.getTo().getAlgoInstance();
					
					if (instanceTo.getContainer() != this)
						res.add(instanceTo);
					
				}
				
			}
		}
		
		
		return res;
		
	}
	
	/**
	 * Lists all the connection which come from outside of this container 
	 * to the inside of this container.
	 * @return
	 */
	@Override
	public Collection<IConnection> getConnectionsComingFromOutside() {
		
		final Collection<IConnection> res = new HashSet<IConnection>();

		for (IAlgoInstance child : children) {
		
			for (IInputOutputInstance input : child.getInputInstances()) {
				
				for (IConnection c: input.getConnections()) {
					
					IAlgoInstance instanceTo = c.getFrom().getAlgoInstance();
					
					if (instanceTo.getContainer() != this && !instanceTo.isContainedInto(this))
						res.add(c);
					
				}
				
			}
		}
		return res;

	}
	

	@Override
	public Collection<IConnection> getConnectionsGoingToOutside() {
		
		final Collection<IConnection> res = new HashSet<IConnection>();

		for (IAlgoInstance child : children) {
		
			for (IInputOutputInstance output : child.getOutputInstances()) {
				
				for (IConnection c: output.getConnections()) {
					
					IAlgoInstance instanceTo = c.getTo().getAlgoInstance();
					
					if (!instanceTo.isContainedInto(this))
						res.add(c);
					
				}
				
			}
		}
		return res;
	}

	
	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		super.checkForRun(res);
		
		// ensure that each output of this algo is linked with a reduce algo
		final Collection<IAlgoInstance> dep = getAlgoInstancesDependingToOurChildren();
		for (IAlgoInstance dependantAlgo : dep) {
			if (!(dependantAlgo.getAlgo() instanceof IReduceAlgo)) {
				// TODO nice(r) message; for instance, add the list of algos which may be used there.
				res.messages.errorUser(
								"algo "+dependantAlgo.getName()+
								", which listens for the container "+
								this.getName()+", is not a Reduce algorithm.", 
								getClass()
								);
			
			}
		}
		
		
				
	}

	@Override
	public boolean canContain(IAlgoInstance bo) {
		// by default, delegates to the meta level
		return ((IAlgoContainer)algo).canContain(bo.getAlgo());
	}

	
}
