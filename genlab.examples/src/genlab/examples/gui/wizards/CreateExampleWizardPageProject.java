package genlab.examples.gui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class CreateExampleWizardPageProject extends WizardNewProjectCreationPage {


	public CreateExampleWizardPageProject() {
		super("GenLab Project");
		setTitle("select the project to create with examples");
		setDescription("create a new GenLab project");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
	}

	@Override
	protected boolean validatePage() {
		// TODO Auto-generated method stub
		return super.validatePage();
	}

	
	

}
