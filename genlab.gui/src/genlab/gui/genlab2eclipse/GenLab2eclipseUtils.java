package genlab.gui.genlab2eclipse;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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

/**
 * Maps genlab resources with eclipse ones.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenLab2eclipseUtils {

	private static Map<URI, IGenlabProject> eclipseProject2genlabProject = new HashMap<URI, IGenlabProject>();
	private static Map<IGenlabProject, URI> genlabProject2eclipseProject = new HashMap<IGenlabProject, URI>();
	private static Map<URI, IProject> uri2eclipseProject = new HashMap<URI, IProject>();
	
	public static IGenlabProject getGenlabProjectForEclipseProject(IProject eclipseProject) {
		IGenlabProject project = eclipseProject2genlabProject.get(eclipseProject.getLocationURI());
		
		if (project == null) {
			// project not loaded yet; load it
			String projectAbsolutePath = eclipseProject.getWorkspace().getRoot().getRawLocation().toOSString()+Path.SEPARATOR+eclipseProject.getFullPath().makeAbsolute().toOSString();
			project = GenlabPersistence.getPersistence().readProject(projectAbsolutePath);
		}
		
		return project;
	}
	
	public static IProject getEclipseProjectForGenlabProject(IGenlabProject genlabProject) {
		
		URI eclipseProjectURI = genlabProject2eclipseProject.get(genlabProject);
		
		if (eclipseProjectURI == null) {
			IProject p = getProjectFromRelativePath(genlabProject.getFolder().getName());
			if (p != null) {
				eclipseProjectURI = p.getLocationURI();
				registerEclipseProjectForGenlabProject(p, genlabProject);
			}
		}

		if (eclipseProjectURI == null) {
			final String msg = "no eclipse project registered for genlab project "+genlabProject;
			GLLogger.errorTech(msg, GenLab2eclipseUtils.class);
			throw new ProgramException(msg);
		}
		
		return uri2eclipseProject.get(eclipseProjectURI);
	}
	
	public static void registerEclipseProjectForGenlabProject(IProject eclipseProject, IGenlabProject genlabProject) {
		eclipseProject2genlabProject.put(eclipseProject.getLocationURI(), genlabProject);
		genlabProject2eclipseProject.put(genlabProject, eclipseProject.getLocationURI());
		uri2eclipseProject.put(eclipseProject.getLocationURI(), eclipseProject);
	}
	

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
		
		String projectName = workflow.getProject().getFolder().getName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFile file = project.getFile(workflow.getRelativeFilename());
		return file;
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
	
}
