package genlab.core.projects;

import genlab.core.algos.IGenlabWorkflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO later: save
 * 
 * @author Samuel THiriot
 *
 */
public class GenlabProject implements IGenlabProject {

	private String baseDirectory;
	private Map<String,Object> key2object = new HashMap<String,Object>();
	private Collection<IGenlabWorkflow> workflows = new ArrayList<IGenlabWorkflow>();
	
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

	@Override
	public Collection<IGenlabWorkflow> getWorkflows() {
		return Collections.unmodifiableCollection(workflows);
	}

	@Override
	public void addWorkflow(IGenlabWorkflow workflow) {
		if (!workflows.contains(workflow))
			workflows.add(workflow);
	}

}
