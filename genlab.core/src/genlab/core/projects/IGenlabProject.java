package genlab.core.projects;

import genlab.core.algos.IGenlabWorkflow;

import java.io.File;
import java.util.Collection;

public interface IGenlabProject {
	
	public String getBaseDirectory();
	
	public File getFolder();
	
	public Object getAttachedObject(String key);

	public void setAttachedObject(String key, Object o);
	
	public Collection<IGenlabWorkflow> getWorkflows();
	
	public void addWorkflow(IGenlabWorkflow workflow);
	
	public String getProjectSavingFilename();
	
}
