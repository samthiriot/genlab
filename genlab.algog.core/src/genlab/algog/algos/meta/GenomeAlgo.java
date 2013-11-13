package genlab.algog.algos.meta;

import org.osgi.framework.Bundle;

import genlab.algog.core.Activator;
import genlab.algog.types.GeneFlowType;
import genlab.algog.types.Genome;
import genlab.algog.types.GenomeFlowType;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.LoopForAlgo;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class GenomeAlgo extends AbstractGeneticAlgo {

	public static final InputOutput<Genome> OUTPUT_GENOME = new InputOutput<Genome>(
			GenomeFlowType.SINGLETON, 
			"out_genome", 
			"genome",
			"anything connected here will be a gene of this specy"
			);
	
	public static final String NAME = "genome";
	
	public GenomeAlgo() {
		super(
				NAME, 
				"genome for a specy"
				);
		
		outputs.add(OUTPUT_GENOME);
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
