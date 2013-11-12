package genlab.core.model.instance;

import genlab.core.commons.IWithAssociatedData;
import genlab.core.commons.IWithAssociatedTransientData;
import genlab.core.model.meta.IAlgo;
import genlab.core.projects.IGenlabProject;

import java.io.File;
import java.util.Collection;

/**
 * 
 * A workflow instance.
 * 
 * Contributors may store objects in order to persist data related to workflows.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public interface IGenlabWorkflowInstance extends IAlgoContainerInstance, IWithAssociatedData, IWithAssociatedTransientData {

	public String getName();
	
	public boolean isValid();
	
	public void addAlgoInstance(IAlgoInstance algoInstance);
	
	public void removeAlgoInstance(IAlgoInstance algoInstance);
	
	public boolean isConnected(IInputOutputInstance from, IInputOutputInstance to);
	
	public IConnection getConnection(IInputOutputInstance from, IInputOutputInstance to);
	
	public void removeConnection(IConnection c);

	public void addConnection(Connection c);

	public IConnection connect(IInputOutputInstance from, IInputOutputInstance to);
	
	public File getFilePersisted();
	
	public void _notifyAlgoChanged(IAlgoInstance ai);
	
	public String getRelativePath();
	
	public String getRelativeFilename();

	public String getFilename();

	/**
	 * Returns the absolute filename
	 * @return
	 */
	public String getAbsolutePath();

	public IGenlabProject getProject();
	
	public Collection<IAlgoInstance> getAlgoInstances();

	public Collection<Connection> getConnections();
	
	public boolean containsAlgoInstance(IAlgoInstance algoInstance);
	
	public boolean containsAlgoInstanceId(String algoInstanceId);
	
	public boolean containsAlgoInstanceName(String algoInstanceName);

	
	/**
	 * Returns the count of algo instances existing for this algo
	 * in this workflow. Notably used to increment the default 
	 * name of algo instances.
	 * @param algo
	 * @return
	 */
	public int getCountOfAlgo(IAlgo algo);

	public IAlgoInstance getAlgoInstanceForId(String algoInstanceId);
	
	public IAlgoInstance getAlgoInstanceForName(String algoInstanceName);

	/**
	 * Checks that everybody is ready for run. 
	 * @return
	 */
	public WorkflowCheckResult checkForRun();
	
	public void addListener(IWorkflowContentListener l);
	public void removeListener(IWorkflowContentListener l);

	
}
