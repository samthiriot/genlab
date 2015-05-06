package genlab.core.exec.server;

import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationResult;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class DistantExecutionResult implements Serializable {

	public final ComputationState computationState;
	public final ListOfMessages messages;
	public final Map<String,Object> id2result; 
	
	public DistantExecutionResult(
						ComputationState computationState,
						IComputationResult computationResult) {
		
		this.computationState = computationState;
		this.messages = computationResult.getMessages();
		
		id2result = new HashMap<String, Object>();
		for (IInputOutputInstance o: computationResult.getResults().keySet()) {
			id2result.put(o.getMeta().getId(), computationResult.getResults().get(o));
		}
		
		
	}

}
