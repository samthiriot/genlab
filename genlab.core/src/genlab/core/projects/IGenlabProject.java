package genlab.core.projects;

import java.io.File;

public interface IGenlabProject {

	public static String KEY_ECLIPSE_PROJECT = "eclipse_project";
	
	public String getBaseDirectory();
	
	public File getFolder();
	
	public Object getAttachedObject(String key);

	public void setAttachedObject(String key, Object o);
	
}
