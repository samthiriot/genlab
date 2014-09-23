package genlab.gui;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
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
	
	/**
	 * Displays a dialog which enables the selection of a workflow in the workspace.
	 * @return
	 */
	public static File dialogSelectWorkflow(Shell shell, IProject project, String title, String message) {

		ElementTreeSelectionDialog elementSelector = new ElementTreeSelectionDialog(
				shell, 
				new WorkbenchLabelProvider() ,
				new BaseWorkbenchContentProvider()
				);
		elementSelector.addFilter(new ProjectsAndWorkflowsFilter());
		elementSelector.setInput(project);
		elementSelector.setTitle(title);
		elementSelector.setMessage(message);
		elementSelector.setAllowMultiple(false);
		//elementSelector.setImage(image);
		elementSelector.setValidator(new ISelectionStatusValidator() {
			
			@Override
			public IStatus validate(Object[] selection) {
				
				if ( 
						(selection.length != 1)
						|| 
						!(selection[0] instanceof File)
						|| 
						(!GenLab2eclipseUtils.isGenlabWorkflow((File)selection[0]))
						)
					return new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
                            "please select a genlab workflow"
							);
				else
					return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
					
			}
		});
		elementSelector.open();
		
		if (elementSelector.getReturnCode() == ElementTreeSelectionDialog.OK){
			File f = (File) elementSelector.getFirstResult();
			//return f.getLocation().toOSString();
			return f;
		}
		return null;
		//else {
		//	return null;
		//}
	}
	
	/**
	 * Displays a dialog which enables the selection of a workflow in the workspace.
	 * @return
	 */
	public static IProject dialogSelectProject(Shell shell, String title, String message) {

		ElementTreeSelectionDialog elementSelector = new ElementTreeSelectionDialog(
				shell, 
				new WorkbenchLabelProvider() ,
				new BaseWorkbenchContentProvider()
				);
		elementSelector.addFilter(new ProjectsFilter());
		elementSelector.setInput(ResourcesPlugin.getWorkspace().getRoot());
		elementSelector.setTitle(title);
		elementSelector.setMessage(message);
		elementSelector.setAllowMultiple(false);
		//elementSelector.setImage(image);
		elementSelector.setValidator(new ISelectionStatusValidator() {
			
			@Override
			public IStatus validate(Object[] selection) {
				
				if ( 
						(selection.length != 1)
						|| 
						!(selection[0] instanceof IProject)
						|| 
						(!GenLab2eclipseUtils.isGenlabProject((IProject)selection[0]))
						)
					return new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
                            "please select a Genlab project"
							);
				else
					return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
					
			}
		});
		elementSelector.open();
		
		if (elementSelector.getReturnCode() == ElementTreeSelectionDialog.OK){
			IProject f = (IProject) elementSelector.getFirstResult();
			//return f.getLocation().toOSString();
			return f;
		}
		return null;
		//else {
		//	return null;
		//}
	}
	
	/**
	 * Returns the workflow displayed / selected in the current display
	 * @return
	 */
	public static IGenlabWorkflowInstance getSelectedWorflow() {
		// retrieve the workflow to run
		IEditorPart part = null;
		try {
			part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (NullPointerException e) {
			GLLogger.errorTech("unable to find an active window, view or editor; unable to run a workflow", Utils.class);
			return null;
		}
		   		
		if (part == null) {
			GLLogger.errorTech("unable to find an active editor; unable to run a workflow", Utils.class);
			return null;
		}
		
		IGenlabWorkflowInstance workflow = null;
		if (part instanceof IWorkflowEditor) {
		
			workflow = ((IWorkflowEditor)part).getEditedWorkflow();
			if (workflow == null) {
				GLLogger.errorTech("no workflow associated with this editor; unable to run the workflow", Utils.class);
				return null;
			}
					// ;GenlabWorkflowInstance.currentTODO;
			
		}
		
		return workflow;
	}
	
	private Utils() {
	}
	

}
