package genlab.graphstream.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.MessageAudience;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.core.usermachineinteraction.TextMessage;
import genlab.graphstream.utils.GraphstreamConvertors;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

public class WattsStrogatzExecution extends AbstractGraphStreamGenerator {

	
	public WattsStrogatzExecution(
			IExecution exec,
			IAlgoInstance algoInst) {
		super(
				exec,
				algoInst, 
				new ComputationProgressWithSteps()
				);
	}

	@Override
	public void run() {
		
		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress);
		
		final Integer size = (Integer) getInputValueForInput(WattsStrogatzAlgo.PARAM_N);
		final Integer nei = (Integer) getInputValueForInput(WattsStrogatzAlgo.PARAM_K);
		final Double p = (Double) getInputValueForInput(WattsStrogatzAlgo.PARAM_P);
			
		try {
		
			// TODO state !
			
			result.addMessage(new TextMessage(MessageLevel.INFO, MessageAudience.USER, getClass(), "test of information"));
			
			BaseGenerator generator = new WattsStrogatzGenerator(size, nei, p);
			
			IGenlabGraph graph = GraphstreamConvertors.loadGraphWithGraphstreamFromGeneratorSource(
					// TODO ???
					"generatedTODO", 
					generator, 
					-1,
					result.getMessages()
					);
			result.setResult(WattsStrogatzAlgo.OUTPUT_GRAPH, graph);
			
			setResult(result);
			progress.setProgressMade(1);
			progress.setComputationState(ComputationState.FINISHED_OK);
						
		} catch (RuntimeException e) {
			
			e.printStackTrace();
			
			result.getMessages().errorUser("something went wrong during the execution", getClass());
			result.getMessages().errorTech("exception catch when running "+e.getMessage(), getClass(), e);
			
			progress.setProgressMade(1);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			
		} 
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
	

}
