package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.IGenlabResource;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.Map;

public class MappingObjects {

	private static Map<IGenlabResource,Object> genlab2graphiti = new HashMap<IGenlabResource,Object>(100);
	private static Map<Object,IGenlabResource> graphiti2genlab = new HashMap<Object,IGenlabResource>(100);
	
	public static void register(Object graphitiObject, IGenlabResource genlabResource) {
		GLLogger.debugTech("associating "+graphitiObject+" with "+genlabResource, MappingObjects.class);
		genlab2graphiti.put(genlabResource, graphitiObject);
		graphiti2genlab.put(graphitiObject, genlabResource);
	}
	
	public static IGenlabResource getGenlabResourceFor(Object graphitiResource) {
		GLLogger.debugTech("retrieving genlab resource for graphiti object: "+graphitiResource, MappingObjects.class);
		return  graphiti2genlab.get(graphitiResource);
	}
	
	public static Object getGraphitiObject(IGenlabResource genlabResource) {
		GLLogger.debugTech("retrieving graphiti object for genlab resource: "+genlabResource, MappingObjects.class);
		return genlab2graphiti.get(genlabResource);
	}
	
	private MappingObjects() {	
	}

}
