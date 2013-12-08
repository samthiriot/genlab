package genlab.algog.algos.meta;

import genlab.algog.algos.exec.GeneticExplorationMonoObjectiveAlgoExec;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class GeneticExplorationAlgo extends AbstractGeneticExplorationAlgo {
	
	public GeneticExplorationAlgo() {
		super(
				"genetic exploration", 
				"evolutionary algorithm based of genetic exploration"
				);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GeneticExplorationMonoObjectiveAlgoExec(
				execution, 
				(GeneticExplorationAlgoContainerInstance)algoInstance
				);
	}

	
}
