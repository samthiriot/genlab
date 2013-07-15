package genlab.core.model.instance;

import genlab.core.model.meta.IAlgo;

/**
 * A specific type of algo which can take a variable number of 
 * inputs and outputs.
 * Listens for its workflow, and reacts to its events:
 * so in ensures there is always a free input (to create a connection)
 * 
 * @author Samuel Thiriot
 *
 */
public class ExtensibleInputsAlgoInstance extends AlgoInstance {

	
	public ExtensibleInputsAlgoInstance(
			IAlgo algo,
			IGenlabWorkflowInstance workflow, 
			String id
			) {
		super(algo, workflow, id);
	
	}

	public ExtensibleInputsAlgoInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
	}
	
	
	

}
