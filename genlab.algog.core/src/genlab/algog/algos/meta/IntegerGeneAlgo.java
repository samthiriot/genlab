package genlab.algog.algos.meta;

import genlab.algog.types.Genome;
import genlab.algog.types.GenomeFlowType;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.IntParameter;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class IntegerGeneAlgo<NumType extends Number> extends AbstractGeneticAlgo {

	public static final InputOutput<Genome> INPUT_GENOME = new InputOutput<Genome>(
			GenomeFlowType.SINGLETON, 
			"in_genome", 
			"genome",
			"the genome which manage this gene",
			false
			);
	
	public static final InputOutput<Integer> OUTPUT_VALUE = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_gene_int", 
			"integer",
			"a gene encoding an integer"
			);

	public static final IntParameter PARAM_MINIMUM = new IntParameter(
			"param_min", 
			"minimim value", 
			"the minimal value", 
			new Integer(0)
			);
	
	public static final IntParameter PARAM_MAXIMUM = new IntParameter(
			"param_max", 
			"maximum value", 
			"the maximal value", 
			new Integer(65535)
			);
	
	
	public IntegerGeneAlgo() {
		super(
				"integer gene", 
				"gene which encodes an integer"
				);
		
		inputs.add(INPUT_GENOME);
		outputs.add(OUTPUT_VALUE);
		
		registerParameter(PARAM_MINIMUM);
		registerParameter(PARAM_MAXIMUM);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		// genes can only be contained into genetic exploration algos
		return (algoInstance.getAlgo() instanceof GeneticExplorationAlgo);
	}
	


	
}
