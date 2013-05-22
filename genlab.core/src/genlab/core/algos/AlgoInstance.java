package genlab.core.algos;

import java.util.Map;

public class AlgoInstance implements IAlgoInstance {

	private final String id;
	private final String algoClassName;
	
	private transient IAlgo algo;
	private transient IGenlabWorkflow workflow;
	
	
	public AlgoInstance(IAlgo algo, IGenlabWorkflow workflow) {
		this.algo = algo;
		this.algoClassName = algo.getClass().getCanonicalName();
		this.id = getAlgo()+"."+System.currentTimeMillis(); // TODO mechanism to generate ids
		this.workflow = workflow;
		if (workflow != null)
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
		if (workflow != null)
			workflow.removeAlgoInstance(this);
	}
	
	public void _setWorkflow(IGenlabWorkflow workflow) {
		this.workflow = workflow;
		if (workflow != null)
			workflow.addAlgoInstance(this);
	}
	
	public void _setAlgo(IAlgo algo) {
		this.algo = algo;
	}
	
	private Object readResolve() {
		algo = ExistingAlgos.getExistingAlgos().getAlgoForClass(algoClassName);
		return this;
	}

}
