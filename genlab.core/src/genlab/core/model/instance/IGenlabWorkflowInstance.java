package genlab.core.model.instance;

import genlab.core.commons.IWithAssociatedData;
import genlab.core.commons.IWithAssociatedTransientData;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;

import java.io.File;
import java.util.Collection;

/**
 * 
 * A workflow instance.
 * 
 * TODO checking: add check that the contain is a DAG
 * 
 * Contributors may store objects in order to persist data related to workflows.
 * 
 * 
 * @author Samuel Thiriot
 * 
 */
public interface IGenlabWorkflowInstance extends IAlgoContainerInstance,
		IWithAssociatedData, IWithAssociatedTransientData {

	public String getName();

	public boolean isValid();

	/**
	 * Please always ensure you defined all the settings of the algo prior to
	 * adding it to the workflow; during that call, an event will be raised,
	 * thus enabling other plugins to react to this add. If the algo is not
	 * complete at that time, the event consumers are going to work on false
	 * information.
	 * 
	 * @param algoInstance
	 */
	public void addAlgoInstance(IAlgoInstance algoInstance);

	public void removeAlgoInstance(IAlgoInstance algoInstance);

	public boolean isConnected(IInputOutputInstance from,
			IInputOutputInstance to);

	public IConnection getConnection(IInputOutputInstance from,
			IInputOutputInstance to);

	public void removeConnection(IConnection c);

	public void addConnection(Connection c);

	public IConnection connect(IInputOutputInstance from,
			IInputOutputInstance to);

	public IConnection connect(IAlgoInstance fromAlgoInstance,
			IInputOutput<?> output, IAlgoInstance toAlgoInstance,
			IInputOutput<?> input);

	public File getFilePersisted();

	public void _notifyAlgoChanged(IAlgoInstance ai);
	
	public String getFilename();

	/**
	 * Returns the absolute filename
	 * 
	 * @return
	 */
	public String getAbsolutePath();

	public Collection<IAlgoInstance> getAlgoInstances();

	public Collection<Connection> getConnections();

	public boolean containsAlgoInstance(IAlgoInstance algoInstance);

	public boolean containsAlgoInstanceId(String algoInstanceId);

	public boolean containsAlgoInstanceName(String algoInstanceName);

	/**
	 * Returns the count of algo instances existing for this algo in this
	 * workflow. Notably used to increment the default name of algo instances.
	 * 
	 * @param algo
	 * @return
	 */
	public int getCountOfAlgo(IAlgo algo);

	public IAlgoInstance getAlgoInstanceForId(String algoInstanceId);

	public IAlgoInstance getAlgoInstanceForName(String algoInstanceName);

	/**
	 * Checks that everybody is ready for run.
	 * 
	 * @return
	 */
	public WorkflowCheckResult checkForRun();

	public void addListener(IWorkflowContentListener l);

	public void removeListener(IWorkflowContentListener l);

	/**
	 * returns a valid Id for this basis of ID (not multithread)
	 * @param basedId
	 * @return
	 */
	public String getNextId(String basedId);

	void dispatchAlgoChange(IAlgoInstance ai);

	public void _setFilename(String absoluteFilename);
	
}
