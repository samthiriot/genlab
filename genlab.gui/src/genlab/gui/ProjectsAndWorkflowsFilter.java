package genlab.gui;

import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A filter usable with a content provider which displays only
 * genlab projects and workflow files.
 * 
 * @author Samuel Thiriot
 *
 */
public class ProjectsAndWorkflowsFilter extends ViewerFilter {

	@Override
	public boolean select(
			Viewer viewer, 
			Object parentElement,
			Object element) {
		
		if (element instanceof File) {
			return GenLab2eclipseUtils.isGenlabWorkflow((File)element);
		} else if (element instanceof IProject) {
			return GenLab2eclipseUtils.isGenlabProject((IProject)element);
			//return project.isAccessible();
		} else if (element instanceof Folder){
			System.out.println("should show ? "+parentElement+" / "+element+" == "+element.getClass());
			return true;
		}
		
		return false;
	}

}
