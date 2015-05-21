package genlab.algog.algos.instance;

import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;

@SuppressWarnings("serial")
public class IntegerGeneInstance extends GeneInstance {

	public IntegerGeneInstance(IntegerGeneAlgo algo, IGenlabWorkflowInstance workflow,
			String id) {
		super(algo, workflow, id);
	}

	public IntegerGeneInstance(IntegerGeneAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	protected void adaptMyselfToTarget(IInputOutputInstance input) {
		super.adaptMyselfToTarget(input);
		
		// adapt to min and max
		IntegerInOut i = (IntegerInOut)input.getMeta();
		if (i.getMin() != null)
			setValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM, i.getMin());
		if (i.getMax() != null)
			setValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM, i.getMax());
	}
	
}
