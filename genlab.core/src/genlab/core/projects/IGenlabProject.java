package genlab.core.projects;

import genlab.core.model.instance.IGenlabWorkflowInstance;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface IGenlabProject {
	
	public String getId();
	
	public String getBaseDirectory();
	
	public File getFolder();
	
	public Object getAttachedObject(String key);

	public Map<String,Object> getAttachedObjects();

	public void setAttachedObject(String key, Object o);
	
	public Collection<IGenlabWorkflowInstance> getWorkflows();
	public IGenlabWorkflowInstance getWorkflowForId(String id);
	public IGenlabWorkflowInstance getWorkflowForFilename(String relativePath);

	public void addWorkflow(IGenlabWorkflowInstance workflow);
	
	public String getProjectSavingFilename();
	
}
