package genlab.gui.graphiti.genlab2graphiti;

import java.util.HashMap;
import java.util.Map;

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
 * Assumes that each id of an element contains the id of its workflow.
 *  
 * see http://www.eclipse.org/forums/index.php/t/266445/
 * 
 * @author Samuel Thiriot
 *
 */

public class GenLabIndependenceSolver implements IIndependenceSolver {

	//private transient Map<String, Object> objectMap = new HashMap<String, Object>();
	//private Set<String> objectIds = new HashSet<String>();
	
	protected Map<String,IGenlabWorkflowInstance> id2workflow = new HashMap<String, IGenlabWorkflowInstance>(50);
	
	public final static GenLabIndependenceSolver singleton = new GenLabIndependenceSolver();
	
	protected GenLabIndependenceSolver() {
		
	}
	
	public void registerWorkflow(IGenlabWorkflowInstance workflow) {
		
		final String id = workflow.getId();
		
		if (id2workflow.containsKey(workflow))
			return;
		
		GLLogger.debugTech("received workflow "+id, getClass());
		id2workflow.put(id, workflow);
		
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

		if (id2workflow.isEmpty()) {
			GLLogger.warnTech("no workflow; so unable to find mapping for key "+key, getClass());
			return null;
		}
		
		Object o = null;
		
		//  maybe this is a workflow ? 
		if (o == null) {
			o = id2workflow.get(key);
		}
		
		// attempt to find a possible workflow parent
		IGenlabWorkflowInstance workflow = null;
		for (String possibleKey : id2workflow.keySet()) {
			if (key.startsWith(possibleKey)) {
				workflow = id2workflow.get(possibleKey);
				break;
			}
			// let's assume this is the good workflow ! 
		}
		
		if (workflow == null) {
			GLLogger.errorTech("unable to find a workflow for key "+key+"; problems ahead", getClass());
			return null;
		}
		
		// maybe this is an algo instance or object into the workflow ? 
		if (o == null) {
			
			// let's search into this workflow
			o = workflow.getAlgoInstanceForId(key);
	
		
		}
		
		// or, maybe this is another resource into the workflow ? 
		// ... like an input output ?
		// TODO to be optimized !
		if (o == null)
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
		if (o == null)
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
	 * TODO delete
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
