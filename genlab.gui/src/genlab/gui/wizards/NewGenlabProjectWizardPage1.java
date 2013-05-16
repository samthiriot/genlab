package genlab.gui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewGenlabProjectWizardPage1 extends WizardNewProjectCreationPage {


	public NewGenlabProjectWizardPage1() {
		super("GenLab Project");
		setTitle("new genlab project");
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
