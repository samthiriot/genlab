package genlab.gui;

import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

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
	
	
	public static CommonNavigator findCommonNavigator(String navigatorViewId)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IViewPart view = page.findView(navigatorViewId);
			if (view != null && view instanceof CommonNavigator)
				return ((CommonNavigator) view);

		}
		return null;
	}

}
