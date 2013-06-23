package genlab.core.model.instance;

import genlab.core.commons.FileUtils;
import genlab.core.commons.GenLabException;
import genlab.core.commons.NotImplementedException;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.WorkflowExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.IOException;
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
public class GenlabWorkflowInstance implements IGenlabWorkflowInstance {

	public static GenlabWorkflowInstance currentTODO = null;
	
	protected Set<IAlgoInstance> algoInstances = new HashSet<IAlgoInstance>();
	protected Set<Connection> connections = new HashSet<Connection>();
	
	private Map<String,IAlgoInstance> id2algoInstance = new HashMap<String, IAlgoInstance>();

	public transient IGenlabProject project;
	public String name;
	public String description;
	private transient String relativeFilename;
	
	protected String id;
	
	private Map<String,Object> key2object = new HashMap<String, Object>();
	
	protected static final Map<String,GenlabWorkflowInstance> id2instance = new HashMap<String, GenlabWorkflowInstance>(); 
	
	public GenlabWorkflowInstance(IGenlabProject project, String name, String description, String relativeFilename) {
		this.id = "genlab.workflow."+name;
		this.project = project;
		this.name = name;
		this.description = description;
		if (relativeFilename.endsWith(GenlabPersistence.EXTENSION_WORKFLOW))
			this.relativeFilename = relativeFilename;
		else
			this.relativeFilename = relativeFilename+GenlabPersistence.EXTENSION_WORKFLOW;
		id2instance.put(id, this);
		currentTODO = this;
	}
	
	public GenlabWorkflowInstance(String id, IGenlabProject project, String name, String description, String relativeFilename) {
		this.id = id;
		this.project = project;
		this.name = name;
		this.description = description;
		if (relativeFilename.endsWith(GenlabPersistence.EXTENSION_WORKFLOW))
			this.relativeFilename = relativeFilename;
		else
			this.relativeFilename = relativeFilename+GenlabPersistence.EXTENSION_WORKFLOW;
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
			throw new GenLabException("Another instance was already added with this id: "+algoInstance.getId());
		}
		id2algoInstance.put(algoInstance.getId(), algoInstance);
		algoInstances.add(algoInstance);
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
	}

	@Override
	public void removeAlgoInstance(IAlgoInstance algoInstance) {
		
		GLLogger.debugTech("removing algo instance "+algoInstance, getClass());
		
		// TODO first remove all connections !
		for (IInputOutputInstance io: algoInstance.getInputInstances()) {
				for (IConnection c : io.getConnections()) {
					removeConnection(c);
				}
		}
		for (IInputOutputInstance io: algoInstance.getOutputInstances()) {
			for (IConnection c : io.getConnections()) {
				removeConnection(c);
			}
		}
	
		
		id2algoInstance.remove(algoInstance.getId());
		algoInstances.remove(algoInstance);

		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
	}


	@Override
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
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
	public Collection<Connection> getConnections() {
		return Collections.unmodifiableCollection(connections);
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
	public void addConnection(Connection c) {
		connections.add(c);
	}

	@Override
	public IConnection connect(IInputOutputInstance from, IInputOutputInstance to) {
	
		// TODO check parameters for link creation !
		
		// check parameters
	
		if (from == null || to == null)
			throw new WrongParametersException("cannot connect to null");
		
		if (!algoInstances.contains(from.getAlgoInstance()) || !algoInstances.contains(to.getAlgoInstance()))
			throw new WrongParametersException("this instance of the algorithm does not belongs the workflow");
		
		if (!from.getAlgoInstance().getAlgo().getOuputs().contains(from.getMeta()))
			throw new WrongParametersException("this output does not belong this algo");
		
		if (!to.getAlgoInstance().getAlgo().getInputs().contains(to.getMeta()))
			throw new WrongParametersException("this input does not belong this algo");
		
		if (!from.getMeta().getType().equals(to.getMeta().getType())) {
			throw new WrongParametersException("unable to connect "+from.getMeta().getType()+" with "+to.getMeta().getType());
		}
		
		// actually create it
		Connection res = new Connection(from, to);
		from.addConnection(res);
		to.addConnection(res);
		
		connections.add(res);
		
		// TODO add it to the workflow !
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		
		return res;
	}


	@Override
	public IAlgo getAlgo() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IAlgoExecution execute(IExecution execution) {
		return new WorkflowExecution(execution, this);
	}

	@Override
	public IGenlabWorkflowInstance getWorkflow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInputOutputInstance> getInputInstances() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Collection<IInputOutputInstance> getOutputInstances() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public IInputOutputInstance getInputInstanceForInput(IInputOutput<?> meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInputOutputInstance getOutputInstanceForOutput(IInputOutput<?> meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete() {
		throw new NotImplementedException();
	}

	@Override
	public Object getValueForParameter(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void setValueForParameter(String name, Object value) {
		throw new NotImplementedException();
	}


	@Override
	public WorkflowCheckResult checkForRun() {
		
		WorkflowCheckResult res = new WorkflowCheckResult();
		
		checkForRun(res);
		
		return res;
	}
	
	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		// first, deleguate checking for each result
		
		for (IAlgoInstance subInstance : algoInstances) {
			
			subInstance.checkForRun(res);
			
		}
		
		// TODO checking of workflows
	}

	@Override
	public boolean isConnected(IInputOutputInstance from,
			IInputOutputInstance to) {

		for (IConnection c : to.getConnections()) {
			if (c.getFrom() == from)
				return true;
		}
		
		return false;
	}

	@Override
	public IConnection getConnection(IInputOutputInstance from,
			IInputOutputInstance to) {
		

		for (IConnection c : to.getConnections()) {
			if (c.getFrom() == from)
				return c;
		}
		
		return null;
		
	}

	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeConnection(IConnection c) {
		GLLogger.debugTech("removing connection "+c, getClass());

		if (!connections.remove(c))
			GLLogger.warnTech("was unable to remove connection "+c, getClass());
	}

	
}
