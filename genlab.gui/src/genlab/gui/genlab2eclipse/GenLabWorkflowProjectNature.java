package genlab.gui.genlab2eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Tags the GenLab eclipse projects so we can identify them.
 * In charge on initializing folders structure in the GUI.
 * local
 * @author Samuel Thiriot
 *
 */
public class GenLabWorkflowProjectNature implements IProjectNature {

	// http://www.developpez.net/forums/d739366/environnements-developpement/eclipse/eclipse-platform/modifier-nature-projet/
	public static final String NATURE_ID = "genlab.gui.genlabProjectNature";
			
    private IProject project;

	public GenLabWorkflowProjectNature() {
	}

	@Override
	public void configure() throws CoreException {
		
		// create default subdirectories (well, not mandatory, but that's a way to help users)
		project.getFolder("workflows").create(false, false, null);
		project.getFolder("inputs").create(false, false, null);
		project.getFolder("outputs").create(false, false, null);
		
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
