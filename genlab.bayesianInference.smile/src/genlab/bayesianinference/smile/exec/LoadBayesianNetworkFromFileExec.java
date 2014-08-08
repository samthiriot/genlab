package genlab.bayesianinference.smile.exec;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.smile.SmileUtils;
import genlab.bayesianinference.smile.algos.LoadBayesianNetworkAlgo;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;

import java.io.File;

public class LoadBayesianNetworkFromFileExec extends AbstractAlgoExecutionOneshot {

	public LoadBayesianNetworkFromFileExec(IExecution exec,
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
		progress.setProgressTotal(1);
		progress.setProgressMade(0);

		try {
			// prepare result
			ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
			setResult(result);
			
			// retrieve params
			final File file = (File)algoInst.getValueForParameter(LoadBayesianNetworkAlgo.PARAM_FILE);
			
			IBayesianNetwork loadedNetwork = SmileUtils.readFromFile(file.getAbsolutePath());
			
			result.setResult(LoadBayesianNetworkAlgo.OUTPUT_BN, loadedNetwork);
			
			progress.setProgressMade(1);
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			messages.errorUser("the network load failed: "+e.getMessage(), getClass(), e);
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
