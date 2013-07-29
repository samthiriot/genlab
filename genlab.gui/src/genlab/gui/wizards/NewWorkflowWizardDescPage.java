package genlab.gui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * TODO add help (see parent)
 * @author Samuel Thiriot
 *
 */
public class NewWorkflowWizardDescPage extends WizardPage implements Listener  {

	protected Text inputName;
	protected Text inputDesc;
	
	
	protected NewWorkflowWizardDescPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setPageComplete(false);

	}

	@Override
	public void createControl(Composite parent) {
		
		final int nbColumns = 2;
		
		Composite c = new Composite(parent, SWT.NONE);
		
		c.setLayout(new GridLayout(2,false));
		
		// get name
		{
			Label name = new Label(c, SWT.NONE);
			name.setFont(parent.getFont());
			name.setText("Name of the workflow");
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = nbColumns;
			name.setLayoutData(gd);
		}
		
		{
			inputName = new Text(c, SWT.BORDER | SWT.SINGLE);
			inputName.setFont(parent.getFont());
			inputName.addListener(SWT.Modify, this);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = nbColumns;
			inputName.setLayoutData(gd);
		}
		
		// description
		{
			Label desc = new Label(c, SWT.NONE);
			desc.setFont(parent.getFont());
			desc.setText("description of the workflow");
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = nbColumns;
			desc.setLayoutData(gd);
		}
		
		{
			inputDesc = new Text(c, SWT.BORDER | SWT.MULTI);
			inputDesc.setFont(parent.getFont());
			inputDesc.addListener(SWT.Modify, this);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
			gd.horizontalSpan = nbColumns;
			inputDesc.setLayoutData(gd);
		}

		// other inits
		validatePage();

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(c);
		
	}
	
	/**
	 * The <code>WizardNewFileCreationPage</code> implementation of this
	 * <code>Listener</code> method handles all events and enablements for
	 * controls on this page. Subclasses may extend.
	 */
	@Override
	public void handleEvent(Event event) {
		setPageComplete(validatePage());
	}

	protected boolean validatePage() {

		boolean valid = true;

		// name
		{
			String name = inputName.getText().trim();
			if (name.isEmpty()) {
				valid = false;
				setErrorMessage("The name can not be empty");
			}
			
			// TODO ensure it is unique
			
		}
		
		// description
		{
			String desc = inputDesc.getText().trim();
			if (desc.isEmpty()) {
				setMessage("Tip: add a description for this workflow to facilitate its use.");
			}
				
		}
		
		if (valid) {
			setMessage(null);
			setErrorMessage(null);
			
		}
		
		return valid;
	}
	
	 public IWizardPage getNextPage() {
		 
		 IWizardPage next = super.getNextPage();
		 
		 // intercept, and transmit the next page the future values
		 ((WizardNewFileCreationPage)next).setFileName(getWorkflowName());
	     
		 return next;
	 }
	
	public String getWorkflowName() {
		return inputName.getText().trim();
	}
	
	public String getWorkflowDesc() {
		return inputDesc.getText().trim();
	}
	

}
