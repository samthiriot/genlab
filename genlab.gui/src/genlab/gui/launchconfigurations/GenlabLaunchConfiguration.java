package genlab.gui.launchconfigurations;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import genlab.core.commons.ProgramException;
import genlab.core.exec.GenlabExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.persistence.GenlabPersistence;
import genlab.gui.Activator;
import genlab.gui.Utils;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.perspectives.RunPerspective;


public class GenlabLaunchConfiguration implements ILaunchConfigurationDelegate {

	public static final String LAUNCH_TYPE_ID = "genlab.gui.launchconfigurations.workflow";

	public static final String GROUP_ID = "genlab.gui.launchconfigurations.workflow.launchgroup";
	
	@Override
	public void launch(
			ILaunchConfiguration configuration, 
			String mode, 
			ILaunch launch, 
			IProgressMonitor monitor) throws CoreException {
		
		if (!mode.equals(org.eclipse.debug.core.ILaunchManager.RUN_MODE))
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Only run mode is accepted"));
		
		final String projectPath = configuration.getAttribute(
				GenlabWorkflowLaunchConfigurationTabFirst.KEY_PROJECT, 
				"haha"
				);
		final String workflowRelativePath = configuration.getAttribute(
				GenlabWorkflowLaunchConfigurationTabFirst.KEY_WORKFLOW, 
				"haha"
				);
		
		final boolean forceExec = configuration.getAttribute(
				GenlabWorkflowLaunchConfigurationTabFirst.KEY_FORCE_EXEC,
				false
				);

		IProject pro = GenLab2eclipseUtils.getProjectFromRelativePath(projectPath);
		File workflowFile = new File(pro.getLocation().toOSString()+File.separator+workflowRelativePath);
		IGenlabWorkflowInstance glWorkflow = null;
		try {
			glWorkflow = GenlabPersistence.getPersistence().getWorkflowForFilename(workflowFile.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			throw new ProgramException("unable to find file for workflow: "+workflowFile, e1);
		}
		final IGenlabWorkflowInstance workflowToRun = glWorkflow;
		
		// change perspective
		// TODO propose user ?
		// TODO integrate in the run action ?
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
				   PlatformUI.getWorkbench().showPerspective(
						   RunPerspective.ID,       
						   PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						   );
					GenlabExecution.runBackgroundWithoutWaiting(
							workflowToRun, 
							forceExec, 
							Utils.getOutputFolderForWorkflow(workflowToRun)
							);

				} catch (WorkbenchException e) {
				   e.printStackTrace();
				}
			}
		});
		
		
		
		//GenLab2eclipseUtils.getGenlabProjectForEclipseProject(eclipseProject)
		//GenlabPersistence.getPersistence().getWorkflowForFilename(filename)
		
	}

}
