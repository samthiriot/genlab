package genlab.algog.algos.meta;

import genlab.algog.algos.instance.MeanSquaredErrorAlgoInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.parameters.Parameter;

public class MeanSquaredErrorAlgo extends AbstractGeneticAlgo implements IAlgoContainer {

	public static final InputOutput<Double> INTPUT_VALUES = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"in_values", 
			"values",
			"values to be computed",
			true
			);
	
	public static final InputOutput<Double> OUTPUT_MEANSQUAREDERROR = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_mse", 
			"mse",
			"mean squared erro"
			);
	
	
	public MeanSquaredErrorAlgo() {
		
		super(
				"mean squared error", 
				"mean squared error between the inputs received and the values provided as parameters"
				);
		
		inputs.add(INTPUT_VALUES);
		outputs.add(OUTPUT_MEANSQUAREDERROR);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {

		return new MeanSquaredErrorAlgoInstance(this, workflow);
	}

	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {

		return new MeanSquaredErrorAlgoInstance(this, workflow, id);
	}

	@Override
	public boolean canContain(IAlgo algo) {
		return true; // TODO maybe not accept loops ?
	}
	


}
