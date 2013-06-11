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
			throw new ProgramException("this connection does not really start on this output");
		super.addConnection(c);
	}
	
}
