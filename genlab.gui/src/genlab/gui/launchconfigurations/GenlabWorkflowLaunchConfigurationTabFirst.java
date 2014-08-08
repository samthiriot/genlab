package genlab.gui.launchconfigurations;

import genlab.gui.Utils;
import genlab.gui.VisualResources;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * FIrst tab of the launch configuration wizard for a workflow.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabWorkflowLaunchConfigurationTabFirst 
								extends AbstractLaunchConfigurationTab 
								implements ILaunchConfigurationTab {

	public static String KEY_PROJECT = "project";
	public static String KEY_WORKFLOW = "workflow";
	public static String KEY_FORCE_EXEC = "force_exec";

	//public static String KEY_CPU_COUNT = "cpus_count";
	
	// private Spinner cpus;
	private Text projectText ;
	private Text workflowText ;
	private Button projectButton;
	private Button workflowButton;
	
	private Button checkboxExecutionForced;
	
	private Composite host;
	private Group groupWorkflow ;
	private Group groupSettings ;
	
	public GenlabWorkflowLaunchConfigurationTabFirst() {
			
	}


	protected IProject getProjectAsIProject() {
		
		return GenLab2eclipseUtils.getProjectFromRelativePath(projectText.getText());
		
	}
	
	protected boolean isProjectOK() {
		return getProjectAsIProject() != null;
	}
	
	protected boolean isWorkflowOK() {
		
		IProject p = getProjectAsIProject();
		IFile f = p.getFile(workflowText.getText());
		
		return GenLab2eclipseUtils.isGenlabWorkflow(f);
	}
	
	private void hasChangedSomething() {

		setDirty(true);
		updateLaunchConfigurationDialog();
		
	}
	
	private void selectProject() {
		
		IProject f = Utils.dialogSelectProject(
				getShell(), 
				"select project", 
				"select the project that contains the workflow to run"
				);
		
		projectText.setText(f.getFullPath().toPortableString());

		updateEnabledStates(); 
		
		hasChangedSomething();
	}
	
	private void updateEnabledStates() {
		
		validate();
		
		// state of the select workflow button
		workflowButton.setEnabled(getProjectAsIProject()!=null);
	}
	
	private void selectWorkflow() {
		
		File f = Utils.dialogSelectWorkflow(
				getShell(), 
				getProjectAsIProject(),
				"workflow select", 
				"select the workflow to run"
				);
		workflowText.setText(f.getProjectRelativePath().toPortableString());

		hasChangedSomething();
		
	}
	
	

	@Override
	public void createControl(Composite parent) {
		
		host = new Composite(parent, SWT.NONE);
		host.setLayout(new GridLayout(1, false));
		
		{
			groupWorkflow = new Group(host, SWT.NONE);
			groupWorkflow.setText("workflow to execute");
			groupWorkflow.setLayout(new GridLayout(3, false));
			groupWorkflow.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

			
			Label projectLabel = new Label(groupWorkflow, SWT.NONE);
			projectLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			projectLabel.setText("project:");
			
			projectText = new Text(groupWorkflow, SWT.BORDER);
			projectText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			projectText.setEnabled(false);
			
			projectButton = new Button(groupWorkflow, SWT.PUSH);
			projectButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			projectButton.setText("Browse ...");
			projectButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectProject();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});

			Label workflowLabel = new Label(groupWorkflow, SWT.NONE);
			workflowLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			workflowLabel.setText("workflow:");
			
			workflowText = new Text(groupWorkflow, SWT.BORDER);
			workflowText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			workflowText.setEnabled(false);
			
			workflowButton = new Button(groupWorkflow, SWT.PUSH);
			workflowButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			workflowButton.setText("Browse ...");
			workflowButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectWorkflow();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		}
		
		{
			groupSettings = new Group(host, SWT.NONE);
			groupSettings.setText("execution behaviour");
			groupSettings.setLayout(new GridLayout(1, false));
			groupSettings.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

			checkboxExecutionForced = new Button(groupSettings, SWT.CHECK);
			checkboxExecutionForced.setText("force execution: compute the algorithms even if they are not connected");
			
		}
		
		host.layout(true);
		
		updateEnabledStates();
		
		setControl(host);
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
		// TODO Auto-generated method stub
		
		// configuration.setAttribute(KEY_CPU_COUNT, Runtime.getRuntime().availableProcessors());
		configuration.setAttribute(KEY_FORCE_EXEC, false);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		try {
			
			// cpus.setSelection(configuration.getAttribute(KEY_CPU_COUNT, 1));
			workflowText.setText(configuration.getAttribute(KEY_WORKFLOW, "")); // TODO
			projectText.setText(configuration.getAttribute(KEY_PROJECT, "")); // TODO
			checkboxExecutionForced.setSelection(configuration.getAttribute(KEY_FORCE_EXEC, false)); 
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateEnabledStates();
		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		//configuration.setAttribute(KEY_CPU_COUNT, cpus.getSelection());

		configuration.setAttribute(KEY_PROJECT, projectText.getText());
		configuration.setAttribute(KEY_WORKFLOW, workflowText.getText());
		configuration.setAttribute(KEY_FORCE_EXEC, checkboxExecutionForced.getSelection());

		
	}

	@Override
	public String getName() {

		return "Main";
	}


	protected void validate() {

		setErrorMessage(null);

		if (!isProjectOK()) {
			setErrorMessage("please select a valid project");
			return;
		} 
		if (!isWorkflowOK()) {
			setErrorMessage("please select a valid workflow");
			return;
		}

	}
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		
		return isProjectOK() && isWorkflowOK();
		
	}


	@Override
	public void dispose() {

		VisualResources.disposeChildrenFirstLevel(groupWorkflow);
		groupWorkflow.dispose();
		VisualResources.disposeChildrenFirstLevel(groupSettings);
		groupSettings.dispose();
		host.dispose();
		
		super.dispose();
		
	}
	
	

}
