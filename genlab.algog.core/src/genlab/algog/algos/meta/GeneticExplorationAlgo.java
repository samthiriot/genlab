package genlab.algog.algos.meta;

import genlab.algog.algos.exec.GeneticExplorationAlgoExec;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.core.Activator;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoContainer;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IAlgo;

import org.osgi.framework.Bundle;

public class GeneticExplorationAlgo extends AlgoContainer {

	public GeneticExplorationAlgo() {
		super(
				"genetic exploration", 
				"evolutionary algorithm based of genetic exploration",
				null, 
				ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getTotalId(), 
				"/icons/dna.gif"
				);

	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GeneticExplorationAlgoExec(
				execution, 
				(GeneticExplorationAlgoContainerInstance)algoInstance
				);
	}

	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		// only permit genetic algos to be stored directly into workflows
		return (algoInstance instanceof IGenlabWorkflowInstance);
	}

	@Override
	public boolean canContain(IAlgo algo) {
		// TODO limit ? avoid loops and reduce algos ?
		return true; 
	}
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new GeneticExplorationAlgoContainerInstance(this, workflow, id); 
	}


	@Override
	public final IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new GeneticExplorationAlgoContainerInstance(this, workflow);
	}

	
}
