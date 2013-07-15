package genlab.core.model.instance;

import genlab.core.commons.ProgramException;
import genlab.core.model.meta.IInputOutput;

public class InputInstance extends InputOutputInstance {

	/**
	 * If true, this input will accept several inputs.
	 */
	protected boolean acceptMultipleInputs = false;
	
	public InputInstance(IInputOutput<?> meta, IAlgoInstance algoInstance) {
		super(meta, algoInstance);
	}

	@Override
	public void addConnection(IConnection c) {
		if (c.getTo() != this)
			throw new ProgramException("this connection does not really ends on this output");
		super.addConnection(c);
	}

	public void setAcceptMultipleInputs(boolean b) {
		this.acceptMultipleInputs = b;
	}
	
	public boolean acceptsMultipleInputs() {
		return acceptMultipleInputs;
	}
	
	@Override
	public boolean acceptsConnectionFrom(IInputOutputInstance from) {

		return 
				// always accept if multiple inputs accepted; or accept only if no incoming connection 
				(acceptMultipleInputs || connections.isEmpty())
				&&
				// of course, should be same types !
				(meta.getType().compliantWith(from.getMeta().getType()))
				;
		
	}

	@Override
	public boolean acceptsConnectionTo(IInputOutputInstance to) {
		// never accept connections to, as this is an input and not an output
		return false;
	}
	
}
