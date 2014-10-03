package genlab.examples.gui.wizards;

import java.io.File;

import genlab.core.commons.ProgramException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;


public class CreateExampleWizardPageProject extends WizardPage {

	private Button radioExisting = null;
	private Button radioCreate = null;
	
	private Text nameNovelProject = null;
	private Table existingProjects = null;
	
	private String defaultName = null;
	
	public CreateExampleWizardPageProject(String pagename, IStructuredSelection selection, String defaultName) {
		super(pagename);
		
		// TODO use structured selection !
		this.defaultName = defaultName;
		
	}

	@Override
	public void createControl(Composite parent) {
		
		
		// create host composite
		Composite host = new Composite(parent, SWT.NONE);
		GridData gdHost = new GridData(SWT.FILL, SWT.FILL, false, false);
		host.setLayoutData(gdHost);
		host.setLayout(new GridLayout(1, false));
		
		SelectionListener list = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablementStates();
				setPageComplete(validatePage());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateEnablementStates();
				setPageComplete(validatePage());
			}
		};
		
		// what about select an existing project ? 
		radioExisting = new Button(host, SWT.RADIO);
		radioExisting.setText("use an existing project");
		radioExisting.addSelectionListener(list);
		radioExisting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// list of projects
		existingProjects = new Table(host, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		existingProjects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// populate projects
		TableItem itemForDefaultProject = null;
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			TableItem ti = new TableItem(existingProjects, SWT.NONE);
			ti.setText(p.getName());
			if (p.isOpen())
				ti.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT));
			else
				ti.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT_CLOSED));
			ti.setData(p);
			
			
			if (p.getName().equals(defaultName))
				itemForDefaultProject = ti;
		}
		existingProjects.addSelectionListener(list);
		
		radioCreate = new Button(host, SWT.RADIO);
		radioCreate.setText("create a novel project in the current workspace");
		radioCreate.addSelectionListener(list);
		radioCreate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		
		nameNovelProject = new Text(host, SWT.BORDER);
		nameNovelProject.setText(defaultName);
		nameNovelProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameNovelProject.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		// select default 
		{
			if (itemForDefaultProject != null) {
				radioExisting.setSelection(true);
				radioCreate.setSelection(false);
				existingProjects.setSelection(itemForDefaultProject);
			} else {
				radioExisting.setSelection(false);
				radioCreate.setSelection(true);
				radioCreate.setText(defaultName);
			}
		}
		
		setControl(host);
		updateEnablementStates();
        setPageComplete(validatePage());

	}
	
	protected void updateEnablementStates() {
		
		final boolean existing = radioExisting.getSelection();
		
		existingProjects.setEnabled(existing);
		nameNovelProject.setEnabled(!existing);
		
	}

	protected boolean validatePage() {

		if (radioExisting.getSelection()) {
			
			if (existingProjects.getSelectionCount() != 1) {
				setErrorMessage(null);
				setMessage("Please select one of the projects");
				return false;
			}
			

		} else {
			
			if (nameNovelProject.getText().length() == 0) {
				setErrorMessage(null);
				setMessage("Please type in the name of the project to create");
				return false;
			}
			
			if (!ResourcesPlugin.getWorkspace().getRoot().getFullPath().isValidSegment(nameNovelProject.getText())) {
				setErrorMessage("invalid name (please avoid special characters");
				return false;
			}
			
			IPath futurePath = ResourcesPlugin.getWorkspace().getRoot().getFullPath().append(nameNovelProject.getText());
			
			try {
				File f2 = new File(ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getPath()+File.separator+futurePath.toOSString());
				if (f2.exists()) {
					setErrorMessage("this project name is already used");
					return false;
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		setMessage(null);
		setErrorMessage(null);
		
		return true;
	}
	
	public IProject getSelectedProject() {
		if (existingProjects.getSelectionCount() != 1)
			throw new ProgramException("no projects or several are selected");
		
		return (IProject) existingProjects.getSelection()[0].getData();
	}
	
	public boolean shouldCreateProject() {
		return radioCreate.getSelection();
	}
	
	public String getNameOfProjectToCreate() {
		return nameNovelProject.getText();
	}

}
