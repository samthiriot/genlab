package genlab.gui.genlab2eclipse;

import genlab.core.commons.FileUtils;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Listens for eclipse events related to resources, like opening projects.
 * 
 * TODO change to: when a project is opened, read the project file if available
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
		
		System.err.println("event : "+event);
		if (
				 event == null 
				 || event.getDelta() == null
				 )
		        return;
		 
		 
		 try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				
			    public boolean visit(IResourceDelta delta) throws CoreException {
			
			    	final IResource resource = delta.getResource();
			        
			    	if (
			    			(delta.getKind() != IResourceDelta.ADDED)
			    			||
			    			(resource.getType() != IResource.FILE)
			    			||
			    			(!resource.getName().equals(GenlabPersistence.FILENAME_PROJECT))
			    			)
			    		return true;
			    	
			        GLLogger.debugTech("attempting to load the project", getClass());
			   
			        // before loading a project, we need to detect all the possible algo instances provided by plugins
			        ExistingAlgos.getExistingAlgos();
			        
			        // read the genlab project 
			        IGenlabProject genlabProject = GenlabPersistence.getPersistence().readProject(
			        		FileUtils.extractPath(resource.getLocation().toOSString())
			        		);
			        
			        // ... and associate it with this eclipse project
			        GenLab2eclipseUtils.registerEclipseProjectForGenlabProject(
			        		resource.getProject(), 
			        		genlabProject
			        		);
			        		
			       
			        return true;
			    }
			 });
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
