package genlab.gui.wizards;

import genlab.core.model.instance.GenlabFactory;
import genlab.core.projects.IGenlabProject;
import genlab.gui.SyncExecWithResult;
import genlab.gui.Utils;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.genlab2eclipse.GenLabWorkflowProjectNature;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

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
