package genlab.algog.algos.meta;

import genlab.algog.types.Genome;
import genlab.algog.types.GenomeFlowType;
import genlab.core.commons.NotImplementedException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.IntParameter;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class AbstractGeneAlgo extends AbstractGeneticAlgo {

	public static final InputOutput<Genome> INPUT_GENOME = new InputOutput<Genome>(
			GenomeFlowType.SINGLETON, 
			"in_genome", 
			"genome",
			"the genome which manage this gene",
			false
			);
	
	public static final DoubleParameter PARAM_PROBA_MUTATION = new DoubleParameter(
			"param_proba_mutation", 
			"mutation probability", 
			"the probability for each gene to have a mutation", 
			new Double(0.01)
			);
	
	static {
		PARAM_PROBA_MUTATION.setMinValue(0.0);
		PARAM_PROBA_MUTATION.setMaxValue(1.0);
		PARAM_PROBA_MUTATION.setStep(0.001);
	}
	
	public AbstractGeneAlgo(String name, String desc) {
		super(
				name, 
				desc
				);
		
		inputs.add(INPUT_GENOME);
		
		registerParameter(PARAM_PROBA_MUTATION);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		throw new NotImplementedException("gene algorithms are not supposed to be executed");
	}

	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		// genes can only be contained into genetic exploration algos
		return (algoInstance.getAlgo() instanceof AbstractGeneticExplorationAlgo);
	}
	

	
}
