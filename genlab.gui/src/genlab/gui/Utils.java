package genlab.gui;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.views.WorkflowRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

public class Utils {

	
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
	
	public static void updateCommonNavigator(String navigatorViewId, Object toUpdate)
	{
		CommonNavigator cn = Utils.findCommonNavigator(navigatorViewId);
		if (cn != null) {
			if (toUpdate instanceof IResource) {
				// resources are different: we have to update them (this is long), and their listeners 
				// (including navigators) will update accordingly
				IResource resToUpdate = (IResource)toUpdate;
				try {
					resToUpdate.refreshLocal(1, null);
				} catch (CoreException e) {
					GLLogger.warnTech("error while refreshing the project navigator : "+e.getMessage(), Utils.class, e);
				}
			} else 
				// juste update the viewer
				cn.getCommonViewer().refresh(toUpdate, false);
			
			// expand this item
			cn.selectReveal(new StructuredSelection(toUpdate));

		}
	}
	
	public static void updateCommonNavigator(String navigatorViewId)
	{
		CommonNavigator cn = Utils.findCommonNavigator(navigatorViewId);
		if (cn != null) {
			cn.getCommonViewer().refresh(); 
		}
	}
	
	public static void setCommonNavigatorInput(String navigatorViewId, Object input)
	{
		CommonNavigator cn = Utils.findCommonNavigator(navigatorViewId);
		if (cn == null) {
			GLLogger.debugTech("the workflow view is closed, can't update it.", Utils.class);
			return;
		}
		ContentViewer cv = cn.getCommonViewer();
		cv.setInput(input);
		cn.selectReveal(new StructuredSelection(input));
		
	}
	
	/**
	 * TODO is it working ?!
	 * @param navigatorViewId
	 * @param elem
	 */
	public static void expandInCommonNavigator(String navigatorViewId, Object elem) {
		CommonNavigator cn = Utils.findCommonNavigator(navigatorViewId);
		if (cn == null) {
			GLLogger.debugTech("the workflow view is closed, can't update it.", Utils.class);
			return;
		}
		cn.selectReveal(new StructuredSelection(elem));
		
	}
	
	private Utils() {
	}
	

}
