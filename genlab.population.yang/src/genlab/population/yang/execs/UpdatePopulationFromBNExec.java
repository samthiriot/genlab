package genlab.population.yang.execs;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.population.yang.YANGAlgosIndividuals;
import genlab.population.yang.algos.UpdateAttributesFromBNAlgo;
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
		// ... population to update
		final IPopulation inputPopulation = (IPopulation)getInputValueForInput(UpdateAttributesFromBNAlgo.INPUT_POPULATION);
		// ... agent type
		final String typename = (String)getInputValueForInput(UpdateAttributesFromBNAlgo.INPUT_TYPENAME);
		// ... bayesian network
		final IBayesianNetwork inputBN = (IBayesianNetwork)getInputValueForInput(UpdateAttributesFromBNAlgo.INPUT_BAYESIAN_NETWORK);
		
				
		// post processing
		// ... clone pop
		final IPopulation outputPopulation = inputPopulation.clonePopulation();
		// ... retrieve the agent type from its name
		final IAgentType inputAgentType = outputPopulation.getPopulationDescription().getAgentTypeForName(typename);
		if (inputAgentType == null) {
			messages.errorUser("no agent type named \""+typename+"\" is defined in the population description", getClass());
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}

		// then start the generation
		try {
			YANGAlgosIndividuals.updatePopulationFromBN(
				(ComputationProgressWithSteps) progress, 
				outputPopulation, 
				inputAgentType, 
				messages, 
				inputBN
				);
		} catch (WrongParametersException e) {
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			progress.setException(e);
			throw e;
		}
		
		result.setResult(UpdateAttributesFromBNAlgo.OUTPUT_POPULATION, outputPopulation);
		
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
