package genlab.core.model.instance;

import genlab.core.commons.FileUtils;
import genlab.core.commons.GenLabException;
import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Not thread safe. 
 * 
 * Nota: for persistence, always use the Persistence genlab class to ensure integrity.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabWorkflowInstance implements IGenlabWorkflowInstance {

	public static GenlabWorkflowInstance currentTODO = null;
	
	protected Set<Connection> connections = new HashSet<Connection>();
	
	private Map<String,IAlgoInstance> id2algoInstance = new HashMap<String, IAlgoInstance>();

	public transient IGenlabProject project;
	public String name;
	public String description;
	private transient String relativeFilename;
	
	protected String id;
	
	private Map<String,Object> key2object = new HashMap<String, Object>();
	
	protected static final Map<String,GenlabWorkflowInstance> id2instance = new HashMap<String, GenlabWorkflowInstance>(); 
	
	private LinkedList<IWorkflowContentListener> listeners = new LinkedList<IWorkflowContentListener>();
	
	private IAlgoContainerInstance container = null;
	
	public GenlabWorkflowInstance(IGenlabProject project, String name, String description, String relativeFilename) {
		
		GLLogger.debugTech("creating worklow instance "+id+" "+super.toString(), getClass());
		
		this.id = "genlab.workflow."+name;
		this.project = project;
		this.name = name;
		this.description = description;
		if (relativeFilename.endsWith(GenlabPersistence.EXTENSION_WORKFLOW))
			this.relativeFilename = relativeFilename;
		else
			this.relativeFilename = relativeFilename+GenlabPersistence.EXTENSION_WORKFLOW;
		
		if (project == null)
			throw new ProgramException("project can not be null");
		
		project.addWorkflow(this);
		
		// remove that later (unicity is not at a static level, only project)
		if (id2instance.containsKey(id))
			throw new WrongParametersException(" workflow instance with id "+id+" already exists ...");
		id2instance.put(id, this);
		
		// TODO remove
		currentTODO = this;
		
		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());
	}
	
	public GenlabWorkflowInstance(String id, IGenlabProject project, String name, String description, String relativeFilename) {
		
		GLLogger.debugTech("creating worklow instance "+id+" "+super.toString(), getClass());
		this.id = id;
		this.project = project;
		this.name = name;
		this.description = description;
		if (relativeFilename.endsWith(GenlabPersistence.EXTENSION_WORKFLOW))
			this.relativeFilename = relativeFilename;
		else
			this.relativeFilename = relativeFilename+GenlabPersistence.EXTENSION_WORKFLOW;
		
		if (project == null)
			throw new ProgramException("project can not be null");
		
		project.addWorkflow(this);
		
		// remove that later (unicity is not at a static level, only project)
		if (id2instance.containsKey(id))
			throw new WrongParametersException(" workflow instance with id "+id+" already exists ...");
		
		id2instance.put(id, this);
		
		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());


		// TODO remove
		currentTODO = this;
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
		GLLogger.debugTech("added  an algo instance to this workflow : "+algoInstance, getClass());

		id2algoInstance.put(algoInstance.getId(), algoInstance);
		
		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());

		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		for (IWorkflowContentListener l: new LinkedList<IWorkflowContentListener>(listeners)) {
			try {
				l.notifyAlgoAdded(algoInstance);
			} catch (RuntimeException e) {
				GLLogger.warnTech("an error was catched during the dispatching of the event", getClass(), e);
			}
		}
		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());

		
	}

	public void _notifyAlgoChanged(IAlgoInstance ai) {
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		for (IWorkflowContentListener l: new LinkedList<IWorkflowContentListener>(listeners)) {
			try {
				l.notifyAlgoChanged(ai);
			} catch (RuntimeException e) {
				GLLogger.warnTech("an error was catched during the dispatching of the event", getClass(), e);
			}
		}
	}
	
	@Override
	public void removeAlgoInstance(IAlgoInstance algoInstance) {
		
		GLLogger.debugTech("removing algo instance "+algoInstance, getClass());
		
		GLLogger.traceTech("removing input connections...", getClass());
		for (IInputOutputInstance io: algoInstance.getInputInstances()) {
				for (IConnection c : new LinkedList<IConnection>(io.getConnections())) {
					removeConnection(c);
				}
		}
		GLLogger.traceTech("removing output connections...", getClass());
		for (IInputOutputInstance io: algoInstance.getOutputInstances()) {
			for (IConnection c : new LinkedList<IConnection>(io.getConnections())) {
				removeConnection(c);
			}
		}
	
		GLLogger.traceTech("actual removal of the algo instance...", getClass());
		id2algoInstance.remove(algoInstance.getId());

		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());

		GLLogger.traceTech("now notifying listeners of removal", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		for (IWorkflowContentListener l: new LinkedList<IWorkflowContentListener>(listeners)) {
			try {
				l.notifyAlgoRemoved(algoInstance);
			} catch (RuntimeException e) {
				GLLogger.warnTech("an error was catched during the dispatching of the event", getClass());
			}
		}
		
		GLLogger.debugTech("I now contain these algos: "+id2algoInstance, getClass());

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
	public Collection<IAlgoInstance> getChildren() {
		return id2algoInstance.values();
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
		project.addWorkflow(this);
	}
	
	public void _setFilename(String relativefilename) {
		this.relativeFilename = relativefilename;
	}

	@Override
	public boolean containsAlgoInstance(IAlgoInstance algoInstance) {
		IAlgoInstance a = id2algoInstance.get(algoInstance.getId());
		return (a != null && a == algoInstance);
	}

	@Override
	public boolean containsAlgoInstanceId(String algoInstanceId) {
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
    	
    	if (project == null)
			throw new ProgramException("project can not be null");
		
		project.addWorkflow(this);
		

		// TODO remove
		currentTODO = this;
		
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
		
		if (!from.acceptsConnectionTo(to))
			throw new WrongParametersException("connection to this node is not accepted");
		
		if (!to.acceptsConnectionFrom(from))
			throw new WrongParametersException("connection from this node is not accepted");
		
		
		if (!id2algoInstance.containsKey(from.getAlgoInstance().getId()) && !id2algoInstance.containsKey(to.getAlgoInstance().getId()))
			throw new WrongParametersException("this instance of the algorithm does not belongs the workflow");
		
		if (!from.getAlgoInstance().getAlgo().getOuputs().contains(from.getMeta()))
			throw new WrongParametersException("this output does not belong this algo");
		
		if (!to.getAlgoInstance().getAlgo().getInputs().contains(to.getMeta()))
			throw new WrongParametersException("this input does not belong this algo");
		
		// actually create it
		Connection res = new Connection(from, to);
		from.addConnection(res);
		to.addConnection(res);
		
		connections.add(res);
		
		// TODO add it to the workflow !
		
		// notify of change
		WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
		for (IWorkflowContentListener l: new LinkedList<IWorkflowContentListener>(listeners)) {
			try {
				l.notifyConnectionAdded(res);
			} catch (RuntimeException e) {
				GLLogger.warnTech("an error was catched during the dispatching of the event", getClass());
			}
		}
		
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
		
		for (IAlgoInstance subInstance : id2algoInstance.values()) {
			
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
		else {
			GLLogger.traceTech("notifying listeners of the removal of this connection..;", getClass());
			WorkflowHooks.getWorkflowHooks().notifyWorkflowChange(this);
			for (IWorkflowContentListener l: new LinkedList<IWorkflowContentListener>(listeners)) {
				try {
					l.notifyConnectionRemoved(c);
				} catch (RuntimeException e) {
					GLLogger.warnTech("an error was catched during the dispatching of the event", getClass());
				}
			}
		}
		
		c.getFrom().removeConnection(c);
		c.getTo().removeConnection(c);
		
	}

	@Override
	public void addListener(IWorkflowContentListener l) {
		if (l == null)
			throw new ProgramException("listeners can not be null");
		if (!listeners.contains(l))
			listeners.add(l);
	}

	@Override
	public void removeListener(IWorkflowContentListener l) {
		listeners.remove(l);
	}

	@Override
	public boolean containsAlgoInstanceName(String algoInstanceName) {
		
		return getAlgoInstanceForName(algoInstanceName) != null;
	}
	

	@Override
	public IAlgoInstance getAlgoInstanceForName(String algoInstanceName) {
		for (IAlgoInstance ai: id2algoInstance.values()) {
			if (algoInstanceName.equals(ai.getName()))
					return ai;
		}
		return null;
	}

	@Override
	public void setName(String novelName) {
		this.name = novelName;
	}

	@Override
	public int getCountOfAlgo(IAlgo algo) {
		int nb = 0;
		for (IAlgoInstance ai: id2algoInstance.values()) {
			if (ai.getAlgo().equals(algo))
				nb++;
		}
		return nb;
	}

	@Override
	public IAlgoContainerInstance getContainer() {
		return container;
	}

	@Override
	public void setContainer(IAlgoContainerInstance container) {
		this.container = container;
	}


	@Override
	public void addChildren(IAlgoInstance child) {
		addAlgoInstance(child);// TODO remove duplicate
	}

	@Override
	public void removeChildren(IAlgoInstance child) {
		removeAlgoInstance(child); // TODO remove duplicate
	}

	@Override
	public Collection<IAlgoInstance> getAlgoInstancesDependingToOurChildren() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public Collection<IConnection> getConnectionsComingFromOutside() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Collection<IConnection> getConnectionsGoingToOutside() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public Collection<IAlgoInstance> getAlgoInstances() {
		return getChildren(); // TODO remove duplicate
	}

	
}
