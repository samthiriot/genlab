package genlab.graphstream.algos.generators;

import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.ComputationResult;
import genlab.core.algos.ComputationState;
import genlab.core.algos.IAlgoInstance;
import genlab.core.usermachineinteraction.MessageAudience;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.core.usermachineinteraction.TextMessage;
import genlab.graphstream.utils.GraphstreamConvertors;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

public class WattsStrogatzExecution extends AbstractGraphStreamGenerator {

	protected final Integer size;
	protected final Integer nei;
	protected final Double p;
	
	
	public WattsStrogatzExecution(
			IAlgoInstance algoInst,
			Integer size,
			Integer nei,
			Double p) {
		super(
				algoInst, 
				new ComputationProgressWithSteps(algoInst.getAlgo())
				);
		this.size = size;
		this.nei = nei;
		this.p = p;
	}

	@Override
	public void run() {
		
		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst.getAlgo(), progress);
		
		// TODO state !
		
		result.addMessage(new TextMessage(MessageLevel.INFO, MessageAudience.USER, "test of information"));
		
		BaseGenerator generator = new WattsStrogatzGenerator(size, nei, p);
		
		IGenlabGraph graph = GraphstreamConvertors.loadGraphWithGraphstreamFromGeneratorSource(
				// TODO ???
				"generatedTODO", 
				generator, 
				-1
				);
		result.setResult(WattsStrogatzAlgo.OUTPUT_GRAPH, graph);
		
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);
		
		setResult(result);
		
	}

	


}
