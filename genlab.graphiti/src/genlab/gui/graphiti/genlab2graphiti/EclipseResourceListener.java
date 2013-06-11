package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Listens for eclipse events related to resource.
 * When a graphiti file is saved, the corresponding genlab workflow is saved too.
 * 
 * TODO removed ??? Better to intercept the editor doSave ? 
 * 
 * @see http://www.eclipse.org/articles/Article-Resource-deltas/resource-deltas.html
 * 
 * @author Samuel Thiriot
 *
 */
public class EclipseResourceListener implements IResourceChangeListener {

	public EclipseResourceListener() {
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		/*
		System.out.println(event);
		System.out.println(event.getType());
		
		 if (
				 event == null 
				 || event.getDelta() == null
				 )
		        return;
		 
		 
		 try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				
			    public boolean visit(IResourceDelta delta) throws CoreException {
			
			    	System.err.println(delta.getResource());
			    	System.err.println(delta.getKind());

			    	final IResource resource = delta.getResource();
			        
			    	if (delta.getKind() != IResourceDelta.CHANGED)
			    		return true;
			    	
			        if (resource.getType() != IResource.FILE)
			            return true;
			        
			        GLLogger.debugTech("attempting to save the workflow", getClass());
			       
			        String modifiedFilename = resource.getLocation().toOSString();
			        String extension = resource.getFileExtension();
			        if (extension == null)
			        	return true;
			        
			        extension = extension.toLowerCase();
			        if (!extension.equals(GraphitiDiagramTypeProvider.GRAPH_EXTENSION))
			        	return true;
			        
			        // now we will suppose we are saving a file related to a workflow
			        IGenlabWorkflow workflow = (IGenlabWorkflow) MappingObjects.getGenlabResourceFor(modifiedFilename);
			        System.err.println("should save workflow: "+workflow);
			        if (workflow == null) {
			        	GLLogger.warnTech("will not save workflow: unable to find the workflow associated with file "+modifiedFilename, getClass());
			        	return true;
			        }
			        // don't only save the workflow, but also the whole project
			        GenlabPersistence.getPersistence().saveProject(workflow.getProject(), false);
			        GenlabPersistence.getPersistence().saveWorkflow(workflow);
			        

			        
			        /*
			        IGenlabProject genlabProject = GenlabPersistence.getPersistence().readProject(
			        		eclipseProject.getWorkspace().getRoot().getLocation().toOSString()+
			        		File.separator+
			        		resource.getLocation().toOSString()
			        		);
			        
			        GenLab2eclipseUtils.registerEclipseProjectForGenlabProject(
			        		eclipseProject, 
			        		genlabProject
			        		);
			        		*/
		/*
			        return true;
			    }
			 });
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
	}

}
