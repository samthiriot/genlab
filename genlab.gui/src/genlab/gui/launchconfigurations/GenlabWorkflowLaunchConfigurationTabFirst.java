package genlab.gui.launchconfigurations;

import java.util.Arrays;

import genlab.core.persistence.GenlabPersistence;
import genlab.gui.Utils;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class GenlabWorkflowLaunchConfigurationTabFirst 
								extends AbstractLaunchConfigurationTab 
								implements ILaunchConfigurationTab {

	public static String KEY_PROJECT = "project";
	public static String KEY_WORKFLOW = "workflow";

	//public static String KEY_CPU_COUNT = "cpus_count";
	
	// private Spinner cpus;
	private Text projectText ;
	private Text workflowText ;
	private Button projectButton;
	private Button workflowButton;
	
	public GenlabWorkflowLaunchConfigurationTabFirst() {
			
	}


	protected IProject getProjectAsIProject() {
		
		return GenLab2eclipseUtils.getProjectFromRelativePath(projectText.getText());
		
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
		
		Composite host = new Composite(parent, SWT.NONE);
		host.setLayout(new GridLayout(1, false));
		
		{
			Group groupWorkflow = new Group(host, SWT.NONE);
			groupWorkflow.setText("workflow to execute");
			groupWorkflow.setLayout(new GridLayout(3, false));
			groupWorkflow.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

			
			Label projectLabel = new Label(groupWorkflow, SWT.NONE);
			projectLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			projectLabel.setText("project:");
			
			projectText = new Text(groupWorkflow, SWT.BORDER);
			projectText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			
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
		
		
		host.layout(true);
		
		updateEnabledStates();
		
		setControl(host);
		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
		// TODO Auto-generated method stub
		
		// configuration.setAttribute(KEY_CPU_COUNT, Runtime.getRuntime().availableProcessors());
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		try {
			
			// cpus.setSelection(configuration.getAttribute(KEY_CPU_COUNT, 1));
			workflowText.setText(configuration.getAttribute(KEY_WORKFLOW, "")); // TODO
			projectText.setText(configuration.getAttribute(KEY_PROJECT, "")); // TODO
			
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
		
	}

	@Override
	public String getName() {

		return "Main";
	}

}
