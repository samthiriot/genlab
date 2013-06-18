package genlab.core.projects;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.persistence.GenlabPersistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO later: save
 * 
 * @author Samuel THiriot
 *
 */
public class GenlabProject implements IGenlabProject {

	private transient String baseDirectory;
	private Map<String,Object> key2object = new HashMap<String,Object>();
	
	private transient Collection<IGenlabWorkflowInstance> workflows = new ArrayList<IGenlabWorkflowInstance>();
	private Collection<String> workflowPathes = new ArrayList<String>();

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
	public Collection<IGenlabWorkflowInstance> getWorkflows() {
		return Collections.unmodifiableCollection(workflows);
	}

	@Override
	public void addWorkflow(IGenlabWorkflowInstance workflow) {
		if (!workflows.contains(workflow)) {
			workflows.add(workflow);
			workflowPathes.add(workflow.getRelativeFilename());
		}
	}

	@Override
	public String getProjectSavingFilename() {
		return baseDirectory+File.separator+GenlabPersistence.FILENAME_PROJECT;
	}
	
	public Collection<String> getWorkflowPathes() {
		if (workflowPathes.isEmpty())
			// quick return
			return Collections.EMPTY_LIST;
		return new LinkedList<String>(workflowPathes);
	}

	public void _setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
	private Object readResolve() {
		workflows = new ArrayList<IGenlabWorkflowInstance>();
		return this;
	}

	@Override
	public Map<String, Object> getAttachedObjects() {
		return Collections.unmodifiableMap(key2object);
	}

}
