package genlab.algog.algos.instance;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.ReceiveFitnessAlgo;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.InputOutputInstance;
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
		LinkedList<IAlgoInstance> genomeAlgos = new LinkedList<IAlgoInstance>();
		int countFitness = 0;
		IAlgoInstance fitnessAlgo = null;
		for (IAlgoInstance aiChild: getChildren()) {
			if (aiChild.getAlgo() instanceof GenomeAlgo) {
				genomeAlgos.add(aiChild);
			} else if (aiChild.getAlgo() instanceof ReceiveFitnessAlgo) {
				countFitness++;
				fitnessAlgo = aiChild;
			} 
		}
		
		if (countFitness != 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain exactly one \""+ReceiveFitnessAlgo.NAME+"\" to receive the fitness of the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());
		}
		if (genomeAlgos.size() < 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain one or more \""+GenomeAlgo.NAME+"\" algo to create the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());			
		}
		
		
		// TODO ensure each set of algos connected to a genome are not interconnected together.
		

		
	}
	

	/**
	 * From the "from" algo, add each children until reaching the fitness evaluation; the output it can be 
	 * retrieved from is returned.
	 * @param from
	 * @param children
	 */
	public IInputOutputInstance collectAlgosToEvaluatePopulation(IAlgoInstance from, Set<IAlgoInstance> children) {
		
		Set<IAlgoInstance> toExplore = new HashSet<IAlgoInstance>();
		
		Set<IAlgoInstance> explored = new HashSet<IAlgoInstance>();
		
		toExplore.add(from);
		
		IInputOutputInstance res = null;
		
		while (!toExplore.isEmpty()) {
			
			IAlgoInstance current = toExplore.iterator().next();
			explored.add(current);
			
			for (IInputOutputInstance outInstance : current.getOutputInstances()) {
				for (IConnection outC: outInstance.getConnections()) {
					IAlgoInstance to = outC.getTo().getAlgoInstance();
					
					if (!explored.contains(to))
						toExplore.add(to);
					
					if (to.getAlgo() instanceof ReceiveFitnessAlgo) {
						// do nothing
						res = outC.getFrom();
						
					} else {
						children.add(to);
					}
				}
			}
			
			toExplore.remove(current);
		}
		
		return res;
	}
	

	
}
