package genlab.algog.algos.instance;

import genlab.algog.algos.meta.DoubleGeneAlgo;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

@SuppressWarnings("serial")
public class DoubleGeneInstance extends GeneInstance {

	public DoubleGeneInstance(DoubleGeneAlgo algo, IGenlabWorkflowInstance workflow,
			String id) {
		super(algo, workflow, id);
	}

	public DoubleGeneInstance(DoubleGeneAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	protected void adaptMyselfToTarget(IInputOutputInstance input) {
		super.adaptMyselfToTarget(input);
		
		// adapt to min and max
		DoubleInOut i = (DoubleInOut)input.getMeta();
		if (i.getMin() != null)
			setValueForParameter(DoubleGeneAlgo.PARAM_MINIMUM, i.getMin());
		if (i.getMax() != null)
			setValueForParameter(DoubleGeneAlgo.PARAM_MAXIMUM, i.getMax());
	}
	
}
