package genlab.gui;

import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A filter usable with a content provider which displays only
 * genlab projects and workflow files.
 * 
 * @author B12772
 *
 */
public class ProjectsFilter extends ViewerFilter {

	@Override
	public boolean select(
			Viewer viewer, 
			Object parentElement,
			Object element) {
		
		if (element instanceof IProject) {
			IProject project = (IProject)element;
			return (
					GenLab2eclipseUtils.isGenlabProject((IProject)element) 
					&& 
					project.isAccessible()
					);
		} else {
			return false;
		}
		
	}
	
	

}
