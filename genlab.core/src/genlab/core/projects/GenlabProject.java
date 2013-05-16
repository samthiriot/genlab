package genlab.core.projects;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GenlabProject implements IGenlabProject {

	private String baseDirectory;
	private Map<String,Object> key2object = new HashMap<String,Object>();
	
	public GenlabProject(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	@Override
	public String getBaseDirectory() {
		
		return baseDirectory;
	}

	@Override
	public File getFolder() {
		return new File(baseDirectory);
	}

	@Override
	public Object getAttachedObject(String key) {
		return key2object.get(key);
	}
	
	@Override
	public void setAttachedObject(String key, Object o) {
		key2object.put(key, o);
	}

}
