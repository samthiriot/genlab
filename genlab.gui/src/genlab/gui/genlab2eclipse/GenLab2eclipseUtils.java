package genlab.gui.genlab2eclipse;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
		return uri2eclipseProject.get(genlabProject2eclipseProject.get(genlabProject));
	}
	
	public static void registerEclipseProjectForGenlabProject(IProject eclipseProject, IGenlabProject genlabProject) {
		eclipseProject2genlabProject.put(eclipseProject.getLocationURI(), genlabProject);
		genlabProject2eclipseProject.put(genlabProject, eclipseProject.getLocationURI());
		uri2eclipseProject.put(eclipseProject.getLocationURI(), eclipseProject);
	}
	

	private GenLab2eclipseUtils() {
		
	}

	public static boolean isGenlabWorkflow(File file) {
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
	
}
