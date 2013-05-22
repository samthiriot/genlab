package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.IGenlabResource;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class MappingObjects {

	private static Map<IGenlabResource,Object> genlab2graphiti = new HashMap<IGenlabResource,Object>(100);
	private static Map<Object,IGenlabResource> graphiti2genlab = new HashMap<Object,IGenlabResource>(100);
	
	public static void register(Object graphitiObject, IGenlabResource genlabResource) {
		GLLogger.debugTech("associating "+graphitiObject+" with "+genlabResource, MappingObjects.class);
		
		if (graphitiObject instanceof Diagram) {
			// specific treatment for Diagrams
			URI toUse = ((Diagram)graphitiObject).eResource().getURI();
			genlab2graphiti.put(genlabResource, toUse);
			graphiti2genlab.put(toUse, genlabResource);
		} 
		genlab2graphiti.put(genlabResource, graphitiObject);
		graphiti2genlab.put(graphitiObject, genlabResource);
	}
	
	public static IGenlabResource getGenlabResourceFor(Object graphitiResource) {
		
		if (graphitiResource instanceof Diagram) {
			// specific treatment for Diagrams
			Diagram diag = (Diagram)graphitiResource;
			return graphiti2genlab.get(diag.eResource().getURI());
		} 
		GLLogger.debugTech("retrieving genlab resource for graphiti object: "+graphitiResource, MappingObjects.class);
		return graphiti2genlab.get(graphitiResource);
	}
	
	public static Object getGraphitiObject(IGenlabResource genlabResource) {
		GLLogger.debugTech("retrieving graphiti object for genlab resource: "+genlabResource, MappingObjects.class);
		return genlab2graphiti.get(genlabResource);
	}
	
	public static IGenlabResource removeGenlabResourceFor(Object graphitiResource) {
		GLLogger.debugTech("removing genlab resource for graphiti object: "+graphitiResource, MappingObjects.class);
		
		IGenlabResource genlabResource = null;
		
		if (graphitiResource instanceof Diagram) {
			// specific treatment for Diagrams
			Diagram diag = (Diagram)graphitiResource;
			genlabResource = graphiti2genlab.remove(diag.eResource().getURI());
		} else {
			genlabResource = graphiti2genlab.remove(graphitiResource);
		}
		genlab2graphiti.remove(genlabResource);
		
		return genlabResource;
		
	}
	
	
	private MappingObjects() {	
	}

}
