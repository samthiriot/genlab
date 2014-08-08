package genlab.population.yang.execs;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.population.yang.YANGAlgos;
import genlab.population.yang.algos.CreatePopulationFromBNAlgo;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;

// TODO !!!
public class UpdatePopulationFromBNExec extends AbstractAlgoExecutionOneshot {

	public UpdatePopulationFromBNExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);

		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		// retrieve inputs
		// ... input count
		final Integer inputCount = (Integer)getInputValueForInput(CreatePopulationFromBNAlgo.INPUT_COUNT);
		if (inputCount == null)
			 throw new WrongParametersException("input count expected");
		progress.setProgressTotal(inputCount);
		// ... bayesian network
		final IBayesianNetwork inputBN = (IBayesianNetwork)getInputValueForInput(CreatePopulationFromBNAlgo.INPUT_BAYESIAN_NETWORK);
		// ... population to fill
		final IPopulation inputPopulation = (IPopulation)getInputValueForInput(CreatePopulationFromBNAlgo.INPUT_POPULATION);

		// ... agent type
		final IAgentType inputAgentType = inputPopulation.getPopulationDescription().getAgentTypes().iterator().next();
				
		
		// then start the generation
		YANGAlgos.fillPopulationFromBN(
				(ComputationProgressWithSteps) progress, 
				inputPopulation, 
				inputAgentType, 
				messages, 
				inputBN, 
				inputCount
				);
		
		result.setResult(CreatePopulationFromBNAlgo.OUTPUT_POPULATION, inputPopulation);
		
		progress.setComputationState(ComputationState.FINISHED_OK);

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

}
