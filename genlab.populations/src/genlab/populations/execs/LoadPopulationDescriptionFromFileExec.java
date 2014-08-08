package genlab.populations.execs;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.populations.algos.LoadPopulationDescriptionFromFileAlgo;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.persistence.PopulationDescriptionPersistence;

import java.io.File;

public class LoadPopulationDescriptionFromFileExec extends
		AbstractAlgoExecutionOneshot {

	public LoadPopulationDescriptionFromFileExec(IExecution exec,
			IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public long getTimeout() {
		return 500;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		try {
			File paramFile = (File)algoInst.getValueForParameter(LoadPopulationDescriptionFromFileAlgo.PARAMETER_FILE);
		
			PopulationDescription desc = PopulationDescriptionPersistence.singleton.readFromFile(paramFile);
			
			result.setResult(LoadPopulationDescriptionFromFileAlgo.OUTPUT_POPULATION_DESCRIPTION, desc);
			
			progress.setComputationState(ComputationState.FINISHED_OK);
		} catch (RuntimeException e) {
			messages.errorUser("error while attempting to load the population description: "+e.getLocalizedMessage(), getClass(), e);
			progress.setException(e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
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
