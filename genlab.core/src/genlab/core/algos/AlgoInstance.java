package genlab.core.algos;

import java.util.Map;

public class AlgoInstance implements IAlgoInstance {

	private final IAlgo algo;
	private final String id;
	
	public AlgoInstance(IAlgo algo) {
		this.algo = algo;
		this.id = getAlgo()+".1"; // TODO mechanism to generate ids
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final IAlgo getAlgo() {
		return algo;
	}

	@Override
	public IAlgoExecution execute(Map<IInputOutput, Object> inputs) {	
		return algo.createExec(this, inputs);
	}

}
