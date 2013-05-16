package genlab.basics.workflow;

import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IInputOutput;

public class Connection {
	
	public final IAlgoInstance algoFrom;
	
	public final IInputOutput<?> fromOutput;
	
	public final IAlgoInstance algoTo;
	
	public final IInputOutput<?> toInput;

	public Connection(IAlgoInstance algoFrom, IInputOutput<?> fromOutput,
			IAlgoInstance algoTo, IInputOutput<?> toInput) {
		super();
		this.algoFrom = algoFrom;
		this.fromOutput = fromOutput;
		this.algoTo = algoTo;
		this.toInput = toInput;
	}

}
