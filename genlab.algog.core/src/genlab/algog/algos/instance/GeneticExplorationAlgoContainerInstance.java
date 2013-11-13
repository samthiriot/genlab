package genlab.algog.algos.instance;

import java.util.LinkedList;

import genlab.algog.algos.meta.GenesAlgo;
import genlab.algog.algos.meta.ReceiveFitnessAlgo;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;

public class GeneticExplorationAlgoContainerInstance extends
		AlgoContainerInstance {

	public GeneticExplorationAlgoContainerInstance(IAlgoContainer algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	public GeneticExplorationAlgoContainerInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		// parent checks how inputs and outputs are connected
		// super.checkForRun(res);
		// we have no connection ^^
		
		// ensure we contain the relevant children
		LinkedList<IAlgoInstance> genesAlgo = new LinkedList<IAlgoInstance>();
		int countFitness = 0;
		IAlgoInstance fitnessAlgo = null;
		for (IAlgoInstance aiChild: getChildren()) {
			if (aiChild.getAlgo() instanceof GenesAlgo) {
				genesAlgo.add(aiChild);
			} else if (aiChild.getAlgo() instanceof ReceiveFitnessAlgo) {
				countFitness++;
				fitnessAlgo = aiChild;
			} 
		}
		
		if (countFitness != 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain exactly one \""+ReceiveFitnessAlgo.NAME+"\" to receive the fitness of the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());
		}
		if (genesAlgo.size() < 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain one or more \""+GenesAlgo.NAME+"\" algo to create the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());			
		}
		
		// check it is connected
		
	}
	

	
}
