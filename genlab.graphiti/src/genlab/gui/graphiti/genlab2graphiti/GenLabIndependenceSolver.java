package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.IGenlabResource;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	private transient Map<String, Object> objectMap = new HashMap<String, Object>();
	private Set<String> objectIds = new HashSet<String>();
	
	
	@Override
	public String getKeyForBusinessObject(Object bo) {
		
		String key = null;
		if (bo instanceof IGenlabResource) {
			IGenlabResource algoInstance = (IGenlabResource)bo;
			key = algoInstance.getId();
		} else {
			key = String.valueOf(bo.hashCode());
		}
				
		if(bo != null) {
			
			if(!objectMap.containsKey(key)) {
				objectMap.put(key, bo);
				objectIds.add(key);
			}
		}
		
		return key;
	}

	@Override
	public Object getBusinessObjectForKey(String key) {

		return objectMap.get(key);
	}
	
	/**
	 * init initial state when deserialized
	 * @return
	 */
	private Object readResolve() {
		
		if (objectMap == null)
			objectMap = new HashMap<String, Object>();
		
		return this;
	}
	
	/**
	 * Enables to restore the state of an independance solver:
	 * takes an ID as a parameter, then returns the corresponding instance.
	 * @param workflow
	 */
	public void _resolveIdsFromWorkflow(IGenlabWorkflow workflow) {
		GLLogger.debugTech("starting to match graphiti saved IDs and genlab workflow instances", getClass());
		for (String key: objectIds) {
			IAlgoInstance algoInstance = workflow.getAlgoInstanceForId(key);
			if (algoInstance != null) {
				objectMap.put(key, algoInstance);
			} else {
				GLLogger.warnTech("error while searching for an algo instance for id "+key, getClass());
			}
		}
	}
	
	

}
