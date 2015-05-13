package genlab.core.exec;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Execution implements IExecution, Externalizable {

	private final ListOfMessages messages;
	
	private boolean forceExecution = false;
	
	private final UniqueTimestamp stamp;
	
	private Map<String, Object> key2value = new HashMap<String, Object>(30);
	
	private IRunner runner = null;
	
	
	public Execution(IRunner runner) {
		
		// init stamp 
		stamp = new UniqueTimestamp();
		
		// create messages 
		messages = new ListOfMessages();
		
		this.runner = runner;
		
		// and register them !
		ListsOfMessages.registerListOfMessages(getId(), messages);
		
	}

	@Override
	public ListOfMessages getListOfMessages() {
		return messages;
	}


	@Override
	public boolean getExecutionForced() {
		return forceExecution;
	}

	@Override
	public void setExecutionForced(boolean f) {
		this.forceExecution = f;		
	}

	@Override
	public String getId() {
		return "execution_"+stamp.toString();
	}

	@Override
	public Map<String, Object> getAllTechnicalInformations() {
		synchronized (key2value) {
			return Collections.unmodifiableMap(key2value);
		}
	}

	@Override
	public void setTechnicalInformation(String key, Object value) {
		synchronized (key2value) {
			key2value.put(key, value);
		}
	}

	@Override
	public Object getTechnicalInformation(String key) {
		synchronized (key2value) {

			return key2value.get(key);
		}
	}

	@Override
	public void incrementTechnicalInformationLong(String key, long increment) {
		
		synchronized (key2value) {
			Long value = (Long)key2value.get(key);
			if (value == null)
				value = increment;
			else 
				value += increment;
			key2value.put(key, value);
				
		}
		
	} 
	
	@Override
	public void incrementTechnicalInformationLong(String key) {
		incrementTechnicalInformationLong(key, 1);
	}
	
	public void displayTechnicalInformationsOnMessages() {
		if (messages == null)
			return;
		StringBuffer sb = new StringBuffer("\n");
		synchronized (key2value) {
			for (Entry<String,Object> e : key2value.entrySet()){
				sb.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
			}
		}
		messages.infoTech("here are some technical informations for this execution: "+sb.toString(), getClass());

	}

	public IRunner getRunner() {
		return runner;
	}

	@Override
	public void setRunner(IRunner r) {
		this.runner = r;
	}

	/**
	 * For serialization only
	 */
	public Execution() {
		
		// init stamp 
		stamp = new UniqueTimestamp();
		
		// create messages 
		messages = new ListOfMessages();

		// and register them !
		ListsOfMessages.registerListOfMessages(getId(), messages);
		
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		
		out.writeBoolean(forceExecution);
		out.writeObject(key2value);
		
	}
	

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		forceExecution = in.readBoolean();
		key2value = (Map<String, Object>) in.readObject();
			
	}


}
