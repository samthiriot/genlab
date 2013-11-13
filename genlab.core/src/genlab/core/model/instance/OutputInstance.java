package genlab.core.model.instance;

import genlab.core.commons.ProgramException;
import genlab.core.model.meta.IInputOutput;

public class OutputInstance extends InputOutputInstance {

	public OutputInstance(IInputOutput<?> meta, IAlgoInstance algoInstance) {
		super(meta, algoInstance);
	}

	@Override
	public void addConnection(IConnection c) {
		if (c.getFrom() != this)
			throw new ProgramException("this connection does not really start on this output: "+c.getFrom()+" != "+this);
		super.addConnection(c);
	}

	@Override
	public boolean acceptsConnectionFrom(IInputOutputInstance from) {
		// never accept connections from, as this is an output and not an input 
		return false;
	}

	@Override
	public boolean acceptsConnectionTo(IInputOutputInstance to) {
		// always accept to be connected elsewhere (will demultiplicate the info if required)
		// of course, should be of the same type :-)
		return meta.getType().compliantWith(to.getMeta().getType());
	}
	
}
