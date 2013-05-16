package genlab.core.projects;

public class ProjectFactory {

	public static IGenlabProject getProject(String baseDirectory) {
		return new GenlabProject(baseDirectory);
	}
	
	private ProjectFactory() {
		
	}

}
