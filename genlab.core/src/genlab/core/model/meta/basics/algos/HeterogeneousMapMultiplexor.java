package genlab.core.model.meta.basics.algos;

import org.osgi.framework.Bundle;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.HeterogeneousMapFlowType;
import genlab.core.model.meta.basics.flowtypes.IHeterogeneousMap;

/**
 * Takes many values as inputs, and 
 * creates an heterogeneous map as output.
 * 
 * @author Samuel Thiriot
 *
 */
public class HeterogeneousMapMultiplexor extends BasicAlgo {

	public static final InputOutput<Object> INPUTS_ANYTHING = new InputOutput<Object>(
			AnythingFlowType.SINGLETON,
			"in_anything", 
			"anything", 
			"values to be multiplexed",
			true
	);
	
	public static final InputOutput<IHeterogeneousMap> OUTPUT_MAP = new InputOutput<IHeterogeneousMap>(
			HeterogeneousMapFlowType.SINGLETON,
			"out_map", 
			"all values multiplexed togeter", 
			"values to be multiplexed"
	);
	
	public HeterogeneousMapMultiplexor() {
		
		super(
				"multiplexer", 
				"pieces several values into one object, easier to manipulate",
				null,
				ExistingAlgoCategories.CASTING.getId(),
				null
				);
		
		inputs.add(INPUTS_ANYTHING);
		outputs.add(OUTPUT_MAP);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new HeterogeneousMapMultiplexorExec(execution, algoInstance, null);
	}
	@Override
	public Bundle getBundle() {
		// TODO Auto-generated method stub
		return null;
	}
}
