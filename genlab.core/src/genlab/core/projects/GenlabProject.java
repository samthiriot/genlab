package genlab.core.projects;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * TODO later: save
 * 
 * @author Samuel THiriot
 *
 */
public class GenlabProject implements IGenlabProject {

	private transient String baseDirectory;
	private Map<String,Object> key2object = new HashMap<String,Object>();
	
	private final Set<String> workflowPathes = new HashSet<String>();
	private transient Map<String,IGenlabWorkflowInstance> id2workflow = null;
	private transient Map<String,IGenlabWorkflowInstance> path2workflow = null;

	/**
	 * Stores, for each path, the corresponding project.
	 * Ensures we do not open the same twice.
	 */
	protected static Map<String,GenlabProject> path2project = new HashMap<String, GenlabProject>();
	
	protected static transient Map<String,GenlabProject> openedProjects = new HashMap<String, GenlabProject>();
	
	public static void registerOpenedProject(GenlabProject project) {
		synchronized (openedProjects) {
			
			if (project.getBaseDirectory() == null)
				throw new WrongParametersException("projects should have a valid base directory");
			
			if (path2project.containsKey(project.getBaseDirectory()))
				throw new WrongParametersException("a project was already created for this path "+project.getBaseDirectory());
			
			if (openedProjects.containsKey(project.getId()))
				throw new WrongParametersException("a project was already created for this id "+project.getId());
			
			openedProjects.put(project.getId(), project);
			path2project.put(project.getBaseDirectory(),project);

		}
	}
	
	public static GenlabProject getProject(String id) {
		synchronized (openedProjects) {
			return openedProjects.get(id);
		}
	}
	
	
	public GenlabProject(String baseDirectory) {
		
		GLLogger.debugTech("creating project instance "+baseDirectory+" "+super.toString(), getClass());
		
		this.baseDirectory = baseDirectory;
		
		registerOpenedProject(this);
		
		GLLogger.debugTech("I ("+super.toString()+") now contain these workflows: "+id2workflow, getClass());
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
		LinkedList<IGenlabWorkflowInstance> l = new LinkedList<IGenlabWorkflowInstance>();
		for (String s: workflowPathes) {
			IGenlabWorkflowInstance i = GenlabPersistence.getPersistence().getWorkflowForFilename(this.getBaseDirectory()+s);
			if (i == null)
				GLLogger.warnTech("unable to find a workflow for path "+s, getClass());
			l.add(i);
		}
		
		return l;
	}
	
	protected Map<String,IGenlabWorkflowInstance> getId2Workflow() {
		
		if (id2workflow == null) {
			id2workflow = new HashMap<String,IGenlabWorkflowInstance>();
			GLLogger.debugTech("init id2workflow "+super.toString(), getClass());
		}
		
		return id2workflow;
	}
	
	protected Map<String,IGenlabWorkflowInstance> getFile2Workflow() {
		
		if (path2workflow == null) {
			path2workflow = new HashMap<String,IGenlabWorkflowInstance>();
			GLLogger.debugTech("init path2workflow "+super.toString(), getClass());
		}
		
		return path2workflow;
	}

	@Override
	public void addWorkflow(IGenlabWorkflowInstance workflow) {
		GLLogger.debugTech("adding a sub workflow "+workflow+" to this project: "+this, getClass());
		if (id2workflow == null || !id2workflow.containsKey(workflow.getId())) {
			workflowPathes.add(workflow.getRelativeFilename());
			getId2Workflow().put(workflow.getId(), workflow);
			getFile2Workflow().put(workflow.getRelativeFilename(), workflow);
		}
		GLLogger.debugTech("I ("+super.toString()+") now contain these workflows: "+id2workflow, getClass());
	}
	
	public IGenlabWorkflowInstance getWorkflowForId(String id) {
		return getId2Workflow().get(id);
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
	

	@Override
	public Map<String, Object> getAttachedObjects() {
		return Collections.unmodifiableMap(key2object);
	}

	@Override
	public String getId() {
		return baseDirectory;
	}

	@Override
	public IGenlabWorkflowInstance getWorkflowForFilename(String relativePath) {
		if (path2workflow == null)
			return null;
		
		return path2workflow.get(relativePath);
	}

	public void removeWorkflow(String relativeFilename) {
		workflowPathes.remove(relativeFilename);
	}



}
