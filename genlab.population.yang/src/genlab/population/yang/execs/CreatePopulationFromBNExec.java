package genlab.population.yang.execs;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.meta.BayesianNetworkFlowType;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.population.yang.YANGAlgosIndividuals;
import genlab.population.yang.algos.CreatePopulationFromBNAlgo;
import genlab.populations.algos.AddEmptyAgentsAlgo;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.populations.flowtypes.PopulationFlowType;

public class CreatePopulationFromBNExec extends AbstractAlgoExecutionOneshot {

	public CreatePopulationFromBNExec(
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
		final IPopulation popOrig = (IPopulation)getInputValueForInput(CreatePopulationFromBNAlgo.INPUT_POPULATION);
		// ... agent type
		final String typename = (String)getInputValueForInput(CreatePopulationFromBNAlgo.INPUT_TYPENAME);

		// post process inputs
		// ... clone the population
		final IPopulation inputPopulation = popOrig.clonePopulation();
		// ... retrieve the agent type from its name
		final IAgentType inputAgentType = popOrig.getPopulationDescription().getAgentTypeForName(typename);
		if (inputAgentType == null) {
			messages.errorUser("no agent type named \""+typename+"\" is defined in the population description", getClass());
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}
					
		// then start the generation
		YANGAlgosIndividuals.fillPopulationFromBN(
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
		progress.setComputationState(ComputationState.FINISHED_CANCEL);

	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

}
