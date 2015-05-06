package genlab.gui.launchconfigurations;

import genlab.core.exec.GenlabExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.projects.IGenlabProject;
import genlab.gui.Activator;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.perspectives.RunPerspective;

import java.io.File;

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
		final String workflowPath = configuration.getAttribute(
				GenlabWorkflowLaunchConfigurationTabFirst.KEY_WORKFLOW, 
				"haha"
				);
		
		final boolean forceExec = configuration.getAttribute(
				GenlabWorkflowLaunchConfigurationTabFirst.KEY_FORCE_EXEC,
				false
				);


		IProject pro = GenLab2eclipseUtils.getProjectFromRelativePath(projectPath);
		IGenlabProject glPro = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(pro);
		final IGenlabWorkflowInstance glWorkflow = glPro.getWorkflowForFilename(File.separator+workflowPath);
		
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
					GenlabExecution.runBackgroundWithoutWaiting(glWorkflow, forceExec);

				} catch (WorkbenchException e) {
				   e.printStackTrace();
				}
			}
		});
		
		
		
		//GenLab2eclipseUtils.getGenlabProjectForEclipseProject(eclipseProject)
		//GenlabPersistence.getPersistence().getWorkflowForFilename(filename)
		
	}

}
