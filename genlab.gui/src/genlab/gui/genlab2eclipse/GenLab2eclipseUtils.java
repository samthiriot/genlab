package genlab.gui.genlab2eclipse;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.gui.Utils;

/**
 * Maps genlab resources with eclipse ones.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenLab2eclipseUtils {

	private static Map<URI, IProject> uri2eclipseProject = new HashMap<URI, IProject>();
	

	private GenLab2eclipseUtils() {
		
	}

	public static boolean isGenlabWorkflow(IFile file) {
		// TODO Auto-generated method stub
		return file.getFileExtension().toLowerCase().equals("glw");
	}

	public static boolean isGenlabProject(IProject iProject) {
		try {
			return iProject.hasNature(GenLabWorkflowProjectNature.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}
	
	/**
	 * returns the Iproject for this path relative to the workspace, or null.
	 */
	public static IProject getProjectFromRelativePath(String relativePath) {
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getProject(relativePath);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static IFile getFileForWorkflow(IGenlabWorkflowInstance workflow) {
		
		IFile[] filesForPath = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(Utils.getEclipseURIForWorkflowFile(workflow));
		
		if (filesForPath.length == 0)
			throw new ProgramException("Unable to find any eclipse file for workflow "+workflow.getAbsolutePath());
		if (filesForPath.length > 1)
			throw new ProgramException("Several eclipse files found for workflow "+workflow.getAbsolutePath());
		
		return filesForPath[0];
	}
	
	/**
	 * Create both an eclipse and genlab project. To be called from SWT thread.
	 * @param name
	 * @param runnableContext
	 * @param location
	 * @return
	 */
	public static IProject createEclipseAndGenlabProject(
												String name, 
												IRunnableContext runnableContext, 
												URI location) {
		
		// get a project handle
		final IProject newProjectHandle =  ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		
		// get a project descriptor

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(name);
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
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(Display.getDefault().getActiveShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		// run the new project creation operation
		try {
			runnableContext.run(false, true, op);
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
	
		IProject newProject = newProjectHandle;

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
		
		// expand in the navigator view
		Utils.expandInCommonNavigator(
				"genlab.gui.views.projectexplorer", 
				newProject
				);
		
		return newProject;
	}
	
}
