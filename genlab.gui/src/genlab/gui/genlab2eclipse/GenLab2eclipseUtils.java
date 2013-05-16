package genlab.gui.genlab2eclipse;

import genlab.core.projects.IGenlabProject;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class GenLab2eclipseUtils {

	public static Map<IProject, IGenlabProject> eclipseProject2genlabProject = new HashMap<IProject, IGenlabProject>();
	
	public static IGenlabProject getGenlabProjectForEclipseProject(IProject eclipseProject) {
		return eclipseProject2genlabProject.get(eclipseProject);
	}
	
	public static void registerEclipseProjectForGenlabProject(IProject eclipseProject, IGenlabProject genlabProject) {
		eclipseProject2genlabProject.put(eclipseProject, genlabProject);
	}
	
	
	private GenLab2eclipseUtils() {
		
	}

}
