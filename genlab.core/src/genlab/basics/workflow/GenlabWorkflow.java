package genlab.basics.workflow;

import genlab.core.algos.AlgoInstance;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.WrongParametersException;
import genlab.core.commons.FileUtils;
import genlab.core.commons.GenLabException;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * Nota: for persistence, always use the Persistence genlab class to ensure integrity.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabWorkflow implements IGenlabWorkflow {

	protected Set<IAlgoInstance> algoInstances = new HashSet<IAlgoInstance>();
	private Map<String,IAlgoInstance> id2algoInstance = new HashMap<String, IAlgoInstance>();

	public transient IGenlabProject project;
	public String name;
	public String description;
	private transient String relativeFilename;
	
	protected String id;
	
	private Map<String,Object> key2object = new HashMap<String, Object>();
	
	protected static final Map<String,GenlabWorkflow> id2instance = new HashMap<String, GenlabWorkflow>(); 
	
	public GenlabWorkflow(IGenlabProject project, String name, String description, String relativeFilename) {
		this.id = "genlab.workflow.";
		this.project = project;
		this.name = name;
		this.description = description;
		this.relativeFilename = relativeFilename;
		project.addWorkflow(this);
		id2instance.put(id, this);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAlgoInstance(IAlgoInstance algoInstance) {
		
		if (id2algoInstance.containsKey(algoInstance.getId())) {
			if (id2algoInstance.get(algoInstance.getId()).equals(algoInstance))
				return; // silent version
			throw new GenLabException("Another instance was already added with this id");
		}
		id2algoInstance.put(algoInstance.getId(), algoInstance);
		algoInstances.add(algoInstance);
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
	}

	@Override
	public void removeAlgoInstance(IAlgoInstance algoInstance) {
		id2algoInstance.remove(algoInstance.getId());
		algoInstances.remove(algoInstance);

		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<IInputOutput> getInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoExecution createExec(AlgoInstance algoInstance, Map<IInputOutput, Object> inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect(
			IAlgoInstance algoFrom, IInputOutput<?> fromOutput,
			IAlgoInstance algoTo, IInputOutput<?> toInput
			) {
		
		// ensure integrity
		
		if (!algoInstances.contains(algoFrom) || !algoInstances.contains(algoTo))
			throw new WrongParametersException("this instance of the algorithm does not belongs the workflow");
		
		if (!algoFrom.getAlgo().getOuputs().contains(fromOutput))
			throw new WrongParametersException("this output does not belong this algo");
		
		if (!algoTo.getAlgo().getInputs().contains(toInput))
			throw new WrongParametersException("this input does not belong this algo");
		
		Connection c = new Connection(algoFrom, fromOutput, algoTo, toInput);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
	}

	@Override
	public File getFilePersisted() {
		return null;
	}

	@Override
	public IGenlabProject getProject() {
		return project;
	}

	@Override
	public String getRelativePath() {
		return FileUtils.extractPath(relativeFilename);
	}

	@Override
	public String getRelativeFilename() {
		return relativeFilename;
	}

	@Override
	public String getFilename() {
		return FileUtils.extractFilename(relativeFilename);
	}

	@Override
	public Collection<IAlgoInstance> getAlgoInstances() {
		return Collections.unmodifiableCollection(algoInstances);
	}



	@Override
	public String getAbsolutePath() {
		StringBuffer sbFile = new StringBuffer();
		
		sbFile.append(getProject().getBaseDirectory());
		sbFile.append(File.separator);
		sbFile.append(getRelativeFilename());
		
		File f = new File(sbFile.toString());
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			GLLogger.warnTech("error while forging the absolute path of this workflow from "+sbFile.toString(), getClass(), e);
			return sbFile.toString();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void _setProject(IGenlabProject project) {
		this.project = project;
	}
	
	public void _setFilename(String relativefilename) {
		this.relativeFilename = relativefilename;
	}

	@Override
	public boolean containsAlgoInstance(IAlgoInstance algoInstance) {
		return algoInstances.contains(algoInstance);
	}

	@Override
	public boolean containsAlgoInstance(String algoInstanceId) {
		return id2algoInstance.containsKey(algoInstanceId);
	}

	@Override
	public IAlgoInstance getAlgoInstanceForId(String algoInstanceId) {
		return id2algoInstance.get(algoInstanceId);
	}

	@Override
	public Object addObjectForKey(String key, Object object, boolean raiseEvent) {
		
		Object res = key2object.put(key, object);
		
		if (raiseEvent)
			WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		
		return res;
	}

	@Override
	public Object addObjectForKey(String key, Object object) {
		return addObjectForKey(key, object, true);
	}
	
	@Override
	public Object getObjectForKey(String key) {
		return key2object.get(key);
	}

	@Override
	public String getId() {
		return id;
	}

    private Object readResolve() {
    	id2instance.put(id, this);
	    return this;
	}

	@Override
	public String getCategoryId() {
		return "TODO workflow";
	}

	
}
