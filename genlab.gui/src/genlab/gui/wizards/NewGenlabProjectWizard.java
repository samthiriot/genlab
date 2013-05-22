package genlab.gui.wizards;

import genlab.basics.workflow.GenlabFactory;
import genlab.core.projects.IGenlabProject;
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

public class NewGenlabProjectWizard extends Wizard implements IWorkbenchWizard {

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

		
		// get a project handle
		final IProject newProjectHandle = page1.getProjectHandle();
		
		// get a project descriptor
		URI location = null;
		if (!page1.useDefaults()) {
			location = page1.getLocationURI();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);
		
		
		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				CreateProjectOperation op = new CreateProjectOperation(
						description, ResourceMessages.NewProject_windowTitle);
				try {
					// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved.  Making this undoable resulted in too many 
					// accidental file deletions.
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException
					&& t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException) t.getCause();
				StatusAdapter status;
				if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					status = new StatusAdapter(
							StatusUtil
									.newStatus(
											IStatus.WARNING,
											NLS
													.bind(
															ResourceMessages.NewProject_caseVariantExistsError,
															newProjectHandle
																	.getName()),
											cause));
				} else {
					status = new StatusAdapter(StatusUtil.newStatus(cause
							.getStatus().getSeverity(),
							ResourceMessages.NewProject_errorMessage, cause));
				}
				status.setProperty(StatusAdapter.TITLE_PROPERTY,
						ResourceMessages.NewProject_errorMessage);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(new Status(
						IStatus.WARNING, IDEWorkbenchPlugin.IDE_WORKBENCH, 0,
						NLS.bind(ResourceMessages.NewProject_internalError, t
								.getMessage()), t));
				status.setProperty(StatusAdapter.TITLE_PROPERTY,
						ResourceMessages.NewProject_errorMessage);
				StatusManager.getManager().handle(status,
						StatusManager.LOG | StatusManager.BLOCK);
			}
			return null;
		}

		newProject = newProjectHandle;

		// add project nature
		try {
			IProjectDescription desc = newProject.getDescription();
			String[] prevNatures = desc.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = GenLabWorkflowProjectNature.NATURE_ID;
			desc.setNatureIds(newNatures);
			newProject.setDescription(desc, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		// also create the corresponding genlab project
		IGenlabProject genlabProject = GenlabFactory.createProject(
				newProject.getLocation().toOSString()
				);
		
		GenLab2eclipseUtils.registerEclipseProjectForGenlabProject(
				newProject, 
				genlabProject
				);
		
		// expand in the navigator view
		Utils.expandInCommonNavigator(
				"genlab.gui.views.projectexplorer", 
				newProject
				);
		
		return newProject;
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
