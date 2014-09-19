package genlab.examples.gui.creation;

import java.io.File;

import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.examples.contributors.ExistingExamples;
import genlab.gui.examples.contributors.IGenlabExample;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;

public class ExamplesCreation {

	public static final String PROJECT_NAME = "examples";
	public static final String PROJECT_RELATIVE_PATH = "/"+PROJECT_NAME;
	
	public ExamplesCreation() {
		// TODO Auto-generated constructor stub
	}
	
	public static IGenlabProject getOrCreateExamplesProject() {
		
		// search for a project with this name 
		IProject eclipseProject = null;
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		for (IProject p: workspace.getRoot().getProjects()) {
			if (p.getName().equals(PROJECT_NAME)) {
				eclipseProject = p;
				break;
			}
		}
		
		// retrieve active window
		if (eclipseProject == null) {
			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null)
				return null; // TODO error
			
			eclipseProject = GenLab2eclipseUtils.createEclipseAndGenlabProject(
					PROJECT_NAME, 
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
					null
					);
		}
		
		
		IGenlabProject glProject = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(eclipseProject);
		if (glProject == null) {
			GLLogger.warnTech("unable to find glproject, trouble ahead...", ExamplesCreation.class);
			return null;
		}
		
		GenlabPersistence.getPersistence().saveProject(glProject);
		
		return glProject;
		
	}
	
	private static String getPathForExample(IGenlabExample example) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("/workflows/").append(example.getFileName()).append(".glw");
		
		return sb.toString();
	}
	

	public static String getPathForExampleResources(IGenlabExample example) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("data/").append(example.getFileName()).append(File.separator);
		
		return sb.toString();
	}
	
	public static IGenlabWorkflowInstance createWorkflow(IGenlabExample example, IGenlabProject glProject) {

		
		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(
				glProject,
				example.getName(), 
				example.getDescription(), 
				getPathForExample(example)
				);
		
		File dirData = new File(glProject.getBaseDirectory()+File.separator+getPathForExampleResources(example));
		dirData.mkdirs();
		
		example.createFiles(dirData);
		
		example.fillInstance(workflow);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(workflow);
		
		GenlabPersistence.getPersistence().saveWorkflow(workflow);
		
		return workflow;
	}
	
	public static void createAllWorkflowExamples() {
		
		// get or create the example project
		IGenlabProject exampleProject = getOrCreateExamplesProject();
			
		if (exampleProject == null)
			return;
		
		// then for each project
		for (IGenlabExample e: ExistingExamples.SINGLETON.getAvailableExamples()) {
			
			IGenlabWorkflowInstance wf = exampleProject.getWorkflowForFilename(getPathForExample(e));
			if (wf == null)
				try {
				createWorkflow(e, exampleProject);
				} catch (RuntimeException ex) {
					GLLogger.warnTech("error while trying to install example "+e, ExamplesCreation.class, ex);
				}
		}
		
	}

}
