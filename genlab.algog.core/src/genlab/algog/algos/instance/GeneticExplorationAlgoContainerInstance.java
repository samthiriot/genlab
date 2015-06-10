package genlab.algog.algos.instance;

import genlab.algog.algos.meta.AbstractGeneAlgo;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.GoalAlgo;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GeneticExplorationAlgoContainerInstance extends
		AlgoContainerInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1463474263307513492L;


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
		for (IAlgoInstance aiChild: getChildren()) {
			if (aiChild.getAlgo() instanceof GenomeAlgo) {
				genomeAlgos.add(aiChild);
			} else if (aiChild.getAlgo() instanceof GoalAlgo) {
				countFitness++;
			} 
		}
		
		if (countFitness < 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain one or more \""+GoalAlgo.NAME+"\" to receive the fitness of the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());
		}
		if (genomeAlgos.size() < 1) {
			res.messages.errorUser("a "+algo.getName()+" has to contain one or more \""+GenomeAlgo.NAME+"\" algo to create the population; you will find it in the category \""+ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS.getName()+"\"", getClass());			
		}
		
		
		// TODO ensure each set of algos connected to a genome are not interconnected together.
		

		
	}
	
	/**
	 * Collects all the goals added into this genetic algo.
	 * @return
	 */
	public Set<IAlgoInstance> collectGoals() {
		
		Set<IAlgoInstance> goalAlgos = new HashSet<IAlgoInstance>();
	
		for (IAlgoInstance child : getChildren()) {
			
			if (child.getAlgo() instanceof GoalAlgo) 
				goalAlgos.add(child);
		}
		
		return goalAlgos;
	}


	/**
	 * From the "from" algo, add each children until reaching the fitness evaluation; the output it can be 
	 * retrieved from is returned.
	 * @param from
	 * @param children
	 */
	public void collectAlgosToEvaluatePopulation(IAlgoInstance from, Set<IAlgoInstance> children, Set<IAlgoInstance> genomeGoalAlgos) {
		
		Set<IAlgoInstance> toExplore = new HashSet<IAlgoInstance>();
		
		Set<IAlgoInstance> explored = new HashSet<IAlgoInstance>();
		
		toExplore.add(from);
				
		while (!toExplore.isEmpty()) {
			
			IAlgoInstance current = toExplore.iterator().next();
			
			explored.add(current);
			
			// all the parents of this algo have to be processed
			if (current.getContainer() != this) {
				toExplore.add(current.getContainer());
				children.add(current.getContainer());
			}
			
			// all the outputs have to be processed
			for (IInputOutputInstance outInstance : current.getOutputInstances()) {
				
				for (IConnection outC: outInstance.getConnections()) {
					IAlgoInstance to = outC.getTo().getAlgoInstance();
					
					if (to.getAlgo() instanceof AbstractGeneAlgo) 
						continue;
					
					if (!explored.contains(to))
						toExplore.add(to);
					
					if (to.getAlgo() instanceof GoalAlgo) {
						
						// found a goal, let's add it.
						genomeGoalAlgos.add(to);
						
					} 
						
					children.add(to);
					
				}
			}
			
			// but also all the inputs !
			for (IInputOutputInstance inInstance : current.getInputInstances()) {
				
				for (IConnection inC: inInstance.getConnections()) {
					IAlgoInstance fromA = inC.getFrom().getAlgoInstance();
					
					if (fromA.getAlgo() instanceof AbstractGeneAlgo) 
						continue;
					if (fromA.getAlgo() instanceof GenomeAlgo) 
						continue;

					if (!explored.contains(fromA))
						toExplore.add(fromA); 
					
					children.add(fromA);
					
				}
			}
			
			
			toExplore.remove(current);
		}
		
	}
	

	
}
