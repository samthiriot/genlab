package genlab.core.model.meta.basics.algos;

import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class AddAttributesToGraphExec extends AbstractAlgoExecutionOneshot {

	public AddAttributesToGraphExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		
	} 
	
	
	@Override
	public long getTimeout() {
		return 0;
	}

	@Override
	public void run() {

		// TODO res
		progress.setComputationState(ComputationState.STARTED);
		progress.setProgressTotal(1);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		
		IGenlabGraph inGraph = (IGenlabGraph) getInputValueForInput(AddAttributesToGraphAlgo.INPUT_GRAPH);
		Map<IConnection,Object> inconnection2value = getInputValuesForInput(AddAttributesToGraphAlgo.INPUT_ANYTHING);
		
		// TODO copy grpah or reuse ?
		IGenlabGraph outGraph = inGraph;
		for (IConnection inConnection: inconnection2value.keySet()) {
			
			final String attributeId = inConnection.getFrom().getMeta().getName();
			// declare the graph attribute
			outGraph.declareGraphAttribute(attributeId, Object.class);
			outGraph.setGraphAttribute(
					attributeId, 
					inconnection2value.get(inConnection)
					);
		}
		
		result.setResult(AddAttributesToGraphAlgo.OUTPUT_GRAPH, outGraph);
		
		setResult(result);
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
