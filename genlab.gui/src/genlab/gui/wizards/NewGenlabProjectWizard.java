package genlab.gui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import genlab.gui.Utils;

public class NewGenlabProjectWizard extends Wizard implements IWorkbenchWizard, INewWizard {

	protected WizardNewProjectCreationPage page1 = null;
	
	protected IProject newProject = null;
	
	public NewGenlabProjectWizard() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void addPages() {
		
		page1 = new NewGenlabProjectWizardPage1();
		addPage(page1);
		
		
	}
	
	protected IProject createProject() {

		URI location = null;
		if (!page1.useDefaults()) {
			location = page1.getLocationURI();
		}
		
		return Utils.createEclipseAndGenlabProject(
				page1.getProjectName(), 
				getContainer(),
				location,
				getShell().getDisplay()
				);
				

	}

	@Override
	public boolean performFinish() {
		createProject();
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}
	
	

}
