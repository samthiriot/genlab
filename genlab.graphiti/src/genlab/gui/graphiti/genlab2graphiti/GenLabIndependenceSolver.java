package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.model.IGenlabResource;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.impl.IIndependenceSolver;

/**
 * 
 * Links a graphiti key with a genlab key.
 * 
 * see http://www.eclipse.org/forums/index.php/t/266445/
 * 
 * @author Samuel Thiriot
 *
 */

public class GenLabIndependenceSolver implements IIndependenceSolver {

	//private transient Map<String, Object> objectMap = new HashMap<String, Object>();
	//private Set<String> objectIds = new HashSet<String>();
	
	protected IGenlabWorkflowInstance workflow = null;
	
	public GenLabIndependenceSolver(IGenlabWorkflowInstance workflow) {
		this.workflow = workflow;
	}
	
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		this.workflow = workflow;
		if (workflow != null) 
			GLLogger.debugTech("received a workflow; I'm now able to play my role", getClass());
	}
	
	@Override
	public String getKeyForBusinessObject(Object bo) {
		
		String key = null;
		if (bo instanceof IGenlabResource) {
			IGenlabResource algoInstance = (IGenlabResource)bo;
			key = algoInstance.getId();
		} else {
			key = String.valueOf(bo.hashCode());
		}
			
		/*
		if(bo != null) {
			
			if(!objectMap.containsKey(key)) {
				objectMap.put(key, bo);
				objectIds.add(key);
			}
		}
		*/
		
		return key;
	}

	@Override
	public Object getBusinessObjectForKey(String key) {

		if (workflow == null) {
			GLLogger.warnTech("no workflow; so unable to find mapping for key "+key, getClass());
			return null;
		}
		
		Object o = null;
		
		// maybe this is a workflow ? 
		o = workflow.getAlgoInstanceForId(key);
	
		// or, maybe this is the workflow ? 
		if (o == null && workflow.getId().equals(key)) {
			return workflow;
		}
		
		// or, maybe this is another resource into the workflow ? 
		// ... like an input output ?
		// TODO to be optimized !
		loopAlgos: for (IAlgoInstance a : workflow.getAlgoInstances()) {
			for (IInputOutputInstance io : a.getInputInstances()) {
				if (io.getId().equals(key)) {
					o = io;
					break loopAlgos;
				}
			}
			for (IInputOutputInstance io : a.getOutputInstances()) {
				if (io.getId().equals(key)) {
					o = io;
					break loopAlgos;
				}
			}
		}
		
		// or, it may be a connection !
		// TODO to be optimized...
		for (IConnection c : workflow.getConnections()) {
			if (c.getId().equals(key)) {
				o = c;
				break;
			}
		}
		
		if (o == null)
			GLLogger.warnTech("unable to find mapping for key "+key, getClass());
		
		return o;
		
	}
	
	/**
	 * init initial state when deserialized
	 * @return
	 */
	private Object readResolve() {
		/*
		if (objectMap == null)
			objectMap = new HashMap<String, Object>();
		*/
		return this;
	}
	
	/**
	 * Enables to restore the state of an independance solver:
	 * takes an ID as a parameter, then returns the corresponding instance.
	 * @param workflow
	 */
	public void _resolveIdsFromWorkflow(IGenlabWorkflowInstance workflow) {
		GLLogger.debugTech("starting to match graphiti saved IDs and genlab workflow instances", getClass());
		/*
		for (String key: objectIds) {
			IAlgoInstance algoInstance = workflow.getAlgoInstanceForId(key);
			if (algoInstance != null) {
				objectMap.put(key, algoInstance);
			} else {
				GLLogger.warnTech("error while searching for an algo instance for id "+key, getClass());
			}
		}
		*/
	}
	

}
