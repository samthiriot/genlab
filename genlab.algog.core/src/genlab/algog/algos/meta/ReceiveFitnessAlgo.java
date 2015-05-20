package genlab.algog.algos.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class ReceiveFitnessAlgo extends AbstractGeneticAlgo {

	public static final InputOutput<Double> INPUT_FITNESS = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"in_fitness", 
			"fitness",
			"the fitness computed for this individual",
			false
			);
	
	public static final String NAME = "receive fitness"; 
	public ReceiveFitnessAlgo() {
		super(
				NAME, 
				"receive the fitness for an individual"
				);
		
		inputs.add(INPUT_FITNESS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean canBeContainedInto(IAlgoContainer algoContainer) {
		// genes can only be contained into genetic exploration algos
		return (algoContainer instanceof AbstractGeneticExplorationAlgo);
	}

	
}
