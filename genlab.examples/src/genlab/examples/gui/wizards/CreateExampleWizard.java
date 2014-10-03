package genlab.examples.gui.wizards;

import genlab.core.commons.ProgramException;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.examples.gui.creation.ExamplesCreation;
import genlab.gui.Utils;
import genlab.gui.examples.contributors.IGenlabExample;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.wizards.SelectProjectWizardPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;

/**
 * Wizard to propose the user to create a new workflow
 * 
 * @author Samuel Thiriot
 *
 */
public class CreateExampleWizard extends Wizard implements IWorkbenchWizard {

	protected CreateExampleWizardPageProject page1 = null;
	protected CreateExampleWizardPageExamples page2 = null;
	
	
	protected IProject newProject = null;
	
	protected IStructuredSelection selection = null;
	
	public CreateExampleWizard() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void addPages() {

		page1 = new CreateExampleWizardPageProject(
				"projet",
				null,
				"examples"
				);
		page1.setTitle("Select project");
		page1.setDescription("Select the project which will host the examples");
		
		
		page2 = new CreateExampleWizardPageExamples();
		addPage(page1);
		addPage(page2);
	}
	
	

	@Override
	public boolean performFinish() {

		
		// retrieve or create project
		IProject targetProject = null;
		if (page1.shouldCreateProject()) {
			targetProject = Utils.createEclipseAndGenlabProject(
					page1.getNameOfProjectToCreate(), 
					getContainer(), 
					null,//ResourcesPlugin.getWorkspace().getRoot().getLocationURI(), 
					getShell().getDisplay()
					);
			// TODO manage errors
		} else {
			targetProject = page1.getSelectedProject();
		}
		
		// now we have the project to use
		// lets create the examples to create
		
		IGenlabProject glProject = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(targetProject);
		if (glProject == null)
			throw new ProgramException("unable to find the GenLab project for this eclipse project");
		
		for (IGenlabExample ex: page2.getExamplesToCreate()) {
			try {
				ExamplesCreation.createWorkflow(ex, glProject);
			} catch (RuntimeException e) {
				GLLogger.errorUser("unable to create the example workflow: "+e, getClass());
			}
		}
		
		return true;
		/*
		try {
			GLLogger.debugTech("Will create examples...", getClass());
	
			IProject eclipseProject = Utils.findEclipseProjectInSelection(selection);
			IGenlabProject glProject = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(eclipseProject);
			if (glProject == null)
				GLLogger.warnTech("unable to find glproject, trouble ahead...", getClass());
			
			IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(
					glProject, 
					page1.getWorkflowName(), 
					page1.getWorkflowDesc(), 
					Utils.getPathRelativeToProject(eclipseProject, page2.getRelativePath().toString())
					);
			
	
			
			return true;
			
		} catch (RuntimeException e) {
			page2.setErrorMessage(e.getMessage());
			return false;
		}
		*/
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	

}
