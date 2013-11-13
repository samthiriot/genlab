package genlab.algog.algos.meta;

import org.osgi.framework.Bundle;

import genlab.algog.core.Activator;
import genlab.algog.types.GeneFlowType;
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
public class GenesAlgo extends AbstractGeneticAlgo {

	public static final InputOutput<Object> OUTPUT_ANYTHING = new InputOutput<Object>(
			GeneFlowType.SINGLETON, 
			"out_genes", 
			"genes",
			"anything connected here will be a gene of this specy"
			);
	
	public static final String NAME = "genes";
	
	public GenesAlgo() {
		super(
				NAME, 
				"genes for a specy"
				);
		
		outputs.add(OUTPUT_ANYTHING);
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
