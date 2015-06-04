package genlab.gui;

import java.io.ByteArrayInputStream;
import java.net.URI;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.ColorRGBParameterValue;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.internal.registry.WizardsRegistryReader;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.wizards.IWizardDescriptor;

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
	
	/**
	 * Opens a wizard of this id.
	 * From http://blog.resheim.net/2010/07/invoking-eclipse-wizard.html
	 * 
	 * @param id
	 */
	public static void openWizard(String id) {
		
		 
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		 
		// If not check if it is an "import wizard".
		if  (descriptor == null) {
		  descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		
		// Or maybe an export wizard
		if  (descriptor == null) {
		  descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		
		if (descriptor == null)
			return; // TODO error ?
		
		try  {
			
			// Then if we have a wizard, open it.
		    IWizard wizard = descriptor.createWizard();
		    WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
		    wd.setTitle(wizard.getWindowTitle());
		    
		    if (wizard instanceof IWorkbenchWizard) {
		    	
		    	IWorkbenchWizard wiz = (IWorkbenchWizard)wizard;

		    	wiz.init(
		    			PlatformUI.getWorkbench(), 
		    			(IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()
		    			);
		    }
		    
		    wd.open();
		  
		} catch  (CoreException e) {
		  e.printStackTrace();
		}
	}
	
	public static IProject createEclipseAndGenlabProject(
			final String projectName, 
			final IRunnableContext ctxt,
			final URI location2,
			final Display display
			) {
				
		SyncExecWithResult<IProject> runnable = new SyncExecWithResult<IProject>() {
			
			protected IProject retrieveResult() {
				return GenLab2eclipseUtils.createEclipseAndGenlabProject(
						projectName, 
						ctxt,
						location2
						);
				
			}

		};
		
		display.syncExec(runnable);
		
		return runnable.getResult();
		
	}

	/*
	public static void registerExtension(String contribution) {
		
		
		ByteArrayInputStream is = new ByteArrayInputStream(contribution.toString().getBytes());
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		Object ut = ((ExtensionRegistry)reg).getTemporaryUserToken();
		
		IContributor cont = ContributorFactoryOSGi.createContributor(
				Activator.getDefault().getBundle()
				);
		
		if (!reg.addContribution(is, cont, false, null, null, ut)) {
			throw new ProgramException("unable to register extension");
		}

	}
	 */
	
	public static RGB getRGB(ColorRGBParameterValue v) {
		return new RGB(v.r, v.g, v.b);
	}
	
	public static ColorRGBParameterValue getColorRGBParameterValue(RGB v) {
		return new ColorRGBParameterValue(v.red, v.green, v.blue);
	}
	
	
	private Utils() {
	}
	
	

}
