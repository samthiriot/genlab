package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;

public class GeneticExplorationOneGeneration extends
		AbstractContainerExecutionSupervisor {

	private GeneticExplorationAlgoContainerInstance geneAlgoInst;
	
	public GeneticExplorationOneGeneration(
			IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst
			) {
		
		super(exec, algoInst);
		
		this.geneAlgoInst = algoInst;
	}

	@Override
	protected void initFirstRun() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startOfIteration() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean shouldContinueRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void endOfRun() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getSuffixForCurrentIteration() {
		// TODO Auto-generated method stub
		return null;
	}

}
