package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * An algorithm which relies on several explicit objectives
 * 
 * @author Samuel Thiriot
 */
public abstract class GeneticExplorationMultiObjectiveAlgoExec 
					extends AbstractGeneticExplorationAlgoExec {

	public GeneticExplorationMultiObjectiveAlgoExec(IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	protected void manageStatisticsForCurrentGeneration(
			Map<AnIndividual, Double[]> result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean hasConverged() {
		// TODO Auto-generated method stub
		return false;
	}


}
