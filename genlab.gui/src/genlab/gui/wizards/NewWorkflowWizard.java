package genlab.gui.wizards;

import genlab.basics.workflow.WorkflowFactory;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Wizard to propose the user to create a new workflow
 * 
 * @author Samuel Thiriot
 *
 */
public class NewWorkflowWizard extends Wizard implements IWorkbenchWizard {

	protected NewWorkflowWizardFilePage page2 = null;
	protected NewWorkflowWizardDescPage page1 = null;

	
	protected IProject newProject = null;
	
	protected IStructuredSelection selection = null;
	
	public NewWorkflowWizard() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void addPages() {

		page1 = new NewWorkflowWizardDescPage("workflow description", "description", null);
		page2 = new NewWorkflowWizardFilePage("toto", selection);
		addPage(page1);
		addPage(page2);
	}
	
	

	@Override
	public boolean performFinish() {
		
		GLLogger.debugTech("Attempting to create a workflow...", getClass());
		
		
		IProject eclipseProject = Utils.findEclipseProjectInSelection(selection);
				
		
		IGenlabWorkflow workflow = WorkflowFactory.createWorkflow(
				GenLab2eclipseUtils.getGenlabProjectForEclipseProject(eclipseProject), 
				page1.getWorkflowName(), 
				page1.getWorkflowDesc(), 
				Utils.getPathRelativeToProject(eclipseProject, page2.getRelativePath().toString())
				);
		

		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	

}
