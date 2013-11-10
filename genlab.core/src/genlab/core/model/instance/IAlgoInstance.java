package genlab.core.model.instance;

import genlab.core.exec.IExecution;
import genlab.core.model.IGenlabResource;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;

import java.util.Collection;

public interface IAlgoInstance extends IGenlabResource {

	public String getId();
	
	/**
	 * The name of an algo instance is provided by the user, 
	 * and helps him to understand its role in the workflow
	 */
	public String getName();

	public void setName(String novelName);
	
	public IAlgo getAlgo();
	
	/**
	 * Returns the executable object.
	 * @return
	 */
	public IAlgoExecution execute(IExecution execution);

	/**
	 * Returns that workflow that contains this algo instance (returns null if no parent).
	 * @return
	 */
	public IGenlabWorkflowInstance getWorkflow();
	
	public void _setWorkflowInstance(IGenlabWorkflowInstance  workflow);
	
	/**
	 * Lists all the inputs of this instance
	 * @return
	 */
	public Collection<IInputOutputInstance> getInputInstances();
	

	public Collection<IConnection> getAllIncomingConnections();
	
	

	/**
	 * Lists all the outputs of this instance
	 * @return
	 */
	public Collection<IInputOutputInstance> getOutputInstances();

	public IInputOutputInstance getInputInstanceForInput(IInputOutput<?> meta);
	
	public IInputOutputInstance getOutputInstanceForOutput(IInputOutput<?> meta);
	
	/**
	 * removes this algo instance (removes for related elements as well, 
	 * including workflow.)
	 */
	public void delete();
	
	public Object getValueForParameter(String name);

	public void setValueForParameter(String name, Object value);
	
	public void checkForRun(WorkflowCheckResult res);

	public IAlgoContainerInstance getContainer();
	
	public void setContainer(IAlgoContainerInstance container);
	
	public IAlgoInstance cloneInContext(IAlgoContainerInstance container);
	
}
