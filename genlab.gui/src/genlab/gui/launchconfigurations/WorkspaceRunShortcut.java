package genlab.gui.launchconfigurations;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Activator;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.genlab2eclipse.GenLabWorkflowProjectNature;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.actions.ILaunchable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

public class WorkspaceRunShortcut implements ILaunchShortcut, ILaunchable {

	public WorkspaceRunShortcut() {

	}

    protected void searchAndLaunch(Object[] search, String mode) {
    	
    	if (!mode.equals(org.eclipse.debug.core.ILaunchManager.RUN_MODE))
    		return;
    	
    	Set<Object> searched = new HashSet<Object>();
    	Set<Object> toSearch = new HashSet<Object>();
    	
    	for (Object o: search)
    		toSearch.add(o);
    	
    	while (!toSearch.isEmpty()) {
    	
    		Iterator<Object> it = toSearch.iterator();
    		Object currentObj = it.next();
    		it.remove();
    		
    		searched.add(currentObj);
    		
    		// is it what we search for ?
    		if (currentObj instanceof File) {
    			File currentFile = (File)currentObj;
    			if (GenLab2eclipseUtils.isGenlabWorkflow(currentFile)) {
    				launch(currentFile);
    				return;
    			}
    		}
    		
    		// it is not what we search for, continue search
    		if (currentObj instanceof IContainer) {
    			try {
					for (IResource r : ((IContainer)currentObj).members()) {
						if (!searched.contains(r))
							toSearch.add(r);
					}
				} catch (CoreException e) {
					
				}
    		}
    	}
    	
    	// nothing found, sorry
    	
    }
    
    
    private ILaunchConfiguration searchExistingLaunchConfiguration(String projectStr, String workflowStr) {
    	
    	  ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(GenlabLaunchConfiguration.LAUNCH_TYPE_ID);

    	try {
			for (ILaunchConfiguration conf : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type)) {
				
				if (
						(projectStr.equals(
								conf.getAttribute(
										GenlabWorkflowLaunchConfigurationTabFirst.KEY_PROJECT, 
										"")
										)
										)
						&&
						(workflowStr.equals(
								conf.getAttribute(
										GenlabWorkflowLaunchConfigurationTabFirst.KEY_WORKFLOW, 
										"")
										)
										)
						) 
					return conf;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
	private void launch(IFile currentFile) {

		System.out.println("should find or create config for "+currentFile);
		
		//ResourcesPlugin.getWorkspace().
		
		String projectStr = currentFile.getProject().getFullPath().toPortableString();
		String workflowStr = currentFile.getProjectRelativePath().toPortableString();

		// search for an existing launch configuration
		 ILaunchConfiguration existingConf = searchExistingLaunchConfiguration(projectStr, workflowStr);
		 
		 if (existingConf == null) {
			 // open existing one
			 ILaunchConfigurationType lct =  DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(GenlabLaunchConfiguration.LAUNCH_TYPE_ID);
			 ILaunchConfigurationWorkingCopy wcopy = null;
			 try {
				 wcopy = lct.newInstance(null, DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(currentFile.getName()));
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 wcopy.setAttribute(GenlabWorkflowLaunchConfigurationTabFirst.KEY_PROJECT, projectStr);
			 wcopy.setAttribute(GenlabWorkflowLaunchConfigurationTabFirst.KEY_WORKFLOW, workflowStr);
			 
			 try {
				existingConf = wcopy.doSave();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 //DebugUITools.openLaunchConfigurationDialogOnGroup(shell, selection, groupIdentifier)
		 DebugUITools.openLaunchConfigurationDialogOnGroup(
				 Display.getCurrent().getActiveShell(), 
				 new StructuredSelection(existingConf), 
				 GenlabLaunchConfiguration.GROUP_ID
			//	 new Status(Status.OK, Activator.PLUGIN_ID, "novel configuration")
				 );
	
		 /*
		 DebugUITools.openLaunchConfigurationPropertiesDialog(
				 Display.getCurrent().getActiveShell(), 
				 existingConf, 
				 GenlabLaunchConfiguration.GROUP_ID
				 );	 
		*/
		
		//ILaunchManager.
	}

	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("should launch from selection "+selection);

		if (selection.isEmpty()) 
			return;

		if (selection instanceof IStructuredSelection) {

			IStructuredSelection sselection = (IStructuredSelection)selection;
			
			System.out.println("should launch from STRUCTUED selection "+selection);
			
			searchAndLaunch(sselection.toArray(), mode);
	    } 
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub
		System.out.println("should launch from editor "+editor);
		
		// we expect Workflow displays to implement this interface
		
		IGenlabWorkflowInstance workflow = null;
		if (editor instanceof IWorkflowEditor) {
		
			workflow = ((IWorkflowEditor)editor).getEditedWorkflow();
			if (workflow == null) {
				GLLogger.errorTech("no workflow associated with this editor; unable to run the workflow", getClass());
				return;
			}
			launch(GenLab2eclipseUtils.getFileForWorkflow(workflow));
		}
		
	}

}
