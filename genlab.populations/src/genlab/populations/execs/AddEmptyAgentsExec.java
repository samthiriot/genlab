package genlab.populations.execs;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.populations.algos.AddEmptyAgentsAlgo;
import genlab.populations.algos.CreateEmptyPopulationAlgo;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.Agent;
import genlab.populations.implementations.basic.Population;

public class AddEmptyAgentsExec extends AbstractAlgoExecutionOneshot {

	public AddEmptyAgentsExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public long getTimeout() {
		return 5000; // TODO timeout ?
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		try {
			
			// retrieve inputs
			final IPopulation popOrig = (Population)getInputValueForInput(AddEmptyAgentsAlgo.INPUT_POPULATION);
			final Integer count = (Integer)getInputValueForInput(AddEmptyAgentsAlgo.INPUT_COUNT);
			final String typename = (String)getInputValueForInput(AddEmptyAgentsAlgo.INPUT_TYPENAME);
			
			// post process inputs
			// ... clone the population
			final IPopulation pop = popOrig.clonePopulation();
			// ... retrieve the agent type from its name
			final IAgentType agentType = pop.getPopulationDescription().getAgentTypeForName(typename);
			
			if (agentType == null) {
				messages.errorUser("no agent type named \""+typename+"\" is defined in the population description", getClass());
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
				return;
			}
				
			
			// generate agents
			pop.startManyOperations();
			for (int i=0; i<count; i++) {
				pop.createAgent(agentType);
			}
			pop.endManyOperations();
			
			result.setResult(AddEmptyAgentsAlgo.OUTPUT_POPULATION, pop);
			
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			progress.setException(e);
		}
		
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
