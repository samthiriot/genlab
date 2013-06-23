package genlab.core.model.instance;

import genlab.core.commons.ProgramException;
import genlab.core.model.meta.IInputOutput;

public class InputInstance extends InputOutputInstance {

	public InputInstance(IInputOutput<?> meta, IAlgoInstance algoInstance) {
		super(meta, algoInstance);
	}

	@Override
	public void addConnection(IConnection c) {
		if (c.getTo() != this)
			throw new ProgramException("this connection does not really ends on this output");
		super.addConnection(c);
	}


	
}
