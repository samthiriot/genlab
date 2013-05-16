package genlab.gui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;

public class Utils {

	private Utils() {
	}
	
	public static IProject findEclipseProjectInSelection(IStructuredSelection selection) {

		for (Object selected : selection.toList()) {
			if (selected instanceof IProject) {
				return (IProject)selected;
			}
			if (selected instanceof IResource) {
				return ((IResource)selected).getProject();
			}
		}
		
		return null;
		
	}
	
	public static String getPathRelativeToProject(IProject eclipseProject, String relativePath) {
		final String projectPath = eclipseProject.getFullPath().toString();
		String pathStr = relativePath;
		if (pathStr.startsWith(projectPath)) {
			pathStr = pathStr.substring(projectPath.length());
		}
		return pathStr;
	}

}
