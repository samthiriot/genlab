package genlab.core.exec;

import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Map;

/**
 * Identifies a unique execution (for the user, a click on "run"), 
 * which often manages sub executions. This execution may have parameters (like: always compute even if this is useless).
 * It is also associated with technical informations (we did X times this operation).
 * It is also attached with a channel of messages. 
 * Looks like an execution context.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IExecution {

	/**
	 * A list of messages that is associated with this execution
	 * @return
	 */
	public ListOfMessages getListOfMessages();
	
	/**
	 * Returns true if computations have to be driven, even
	 * if their result is not used (false by default)
	 * @return
	 */
	public boolean getExecutionForced();
	
	public void setExecutionForced(boolean f);
	
	public String getId();
	
	public Map<String,Object> getAllTechnicalInformations();
	
	public void setTechnicalInformation(String key, Object value);
	
	public Object getTechnicalInformation(String key);
	
	public void incrementTechnicalInformationLong(String key, long increment);
	public void incrementTechnicalInformationLong(String key);

	public void displayTechnicalInformationsOnMessages();
	
	public IRunner getRunner();
	
	public void setRunner(IRunner r);
	
	
}
