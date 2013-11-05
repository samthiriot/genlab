package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.basics.flowtypes.HeterogeneousMap;

import java.util.Map;

public class HeterogeneousMapMultiplexorExec extends AbstractAlgoExecutionOneshot {

	public HeterogeneousMapMultiplexorExec(IExecution exec, IAlgoInstance algoInst, IComputationProgress progress) {
		super(exec, algoInst, progress);
	}

	@Override
	public void run() {

		// notify of the start of the task
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		// define result
		final ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		final HeterogeneousMap map = new HeterogeneousMap();
		
		// retrieve the data to be processed ...
		final Map<IConnection,Object> values = getInputValuesForInput(HeterogeneousMapMultiplexor.INPUTS_ANYTHING);
		
		for (IConnection c: values.keySet()) {
			map.put(c.getId(), values.get(c));
		}
		
		result.setResult(
				algoInst.getOutputInstanceForOutput(HeterogeneousMapMultiplexor.OUTPUT_MAP), 
				map
				);
		
		// and, of the end.
		setResult(result);
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "multiplexor execution";
	}

	@Override
	public long getTimeout() {
		return 1000;
	}

}
