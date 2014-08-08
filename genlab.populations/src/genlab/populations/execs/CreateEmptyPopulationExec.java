package genlab.populations.execs;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.populations.algos.CreateEmptyPopulationAlgo;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.Population;

public class CreateEmptyPopulationExec extends AbstractAlgoExecutionOneshot {

	public CreateEmptyPopulationExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public long getTimeout() {
		return 5000;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		try {
			
			// retrieve inputs
			PopulationDescription inputDescription = (PopulationDescription) getInputValueForInput(CreateEmptyPopulationAlgo.INPUT_POPULATION_DESCRIPTION);
			
			Population pop = new Population(inputDescription);
			
			result.setResult(CreateEmptyPopulationAlgo.OUTPUT_POPULATION, pop);
			
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
