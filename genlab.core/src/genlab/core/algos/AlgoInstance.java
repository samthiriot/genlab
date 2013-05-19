package genlab.core.algos;

import java.util.Map;

public class AlgoInstance implements IAlgoInstance {

	private final IAlgo algo;
	private final String id;
	
	private final transient IGenlabWorkflow workflow;
	
	public AlgoInstance(IAlgo algo, IGenlabWorkflow workflow) {
		this.algo = algo;
		this.id = getAlgo()+".1"; // TODO mechanism to generate ids
		this.workflow = workflow;
		workflow.addAlgoInstance(this);
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

	@Override
	public IGenlabWorkflow getWorkflow() {
		return workflow;
	}

	@Override
	public String getName() {
		return algo.getName();
	}

	@Override
	public void delete() {
		workflow.removeAlgoInstance(this);
	}

}
