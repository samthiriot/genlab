package genlab.gui.genlab2eclipse;

import genlab.core.projects.IGenlabProject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class GenLab2eclipseUtils {

	private static Map<URI, IGenlabProject> eclipseProject2genlabProject = new HashMap<URI, IGenlabProject>();
	private static Map<IGenlabProject, URI> genlabProject2eclipseProject = new HashMap<IGenlabProject, URI>();
	private static Map<URI, IProject> uri2eclipseProject = new HashMap<URI, IProject>();
	
	public static IGenlabProject getGenlabProjectForEclipseProject(IProject eclipseProject) {
		return eclipseProject2genlabProject.get(eclipseProject.getLocationURI());
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

}
