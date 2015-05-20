package genlab.networkoperations.exec;


import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.networkoperations.algos.AddChainsAlgo;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public class AddChainsExec extends AbstractAlgoExecutionOneshot {


	public AddChainsExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		
	} 
	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		
		// load parameters
		final IGenlabGraph inGraph = (IGenlabGraph) getInputValueForInput(AddChainsAlgo.INPUT_GRAPH);
		final Integer count = (Integer) getInputValueForInput(AddChainsAlgo.INPUT_COUNT);
		final Integer length = (Integer) getInputValueForInput(AddChainsAlgo.INPUT_LENGTH);
		
		final Long seed = (Long) algoInst.getValueForParameter(AddChainsAlgo.PARAM_SEED);
		
		int totalToDo = count*length;
		
		progress.setProgressTotal(1+totalToDo);
		progress.setComputationState(ComputationState.STARTED);
			
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		// quick exit ? 
		if (count == 0 || length == 0) {
			result.setResult(AddChainsAlgo.OUTPUT_GRAPH, inGraph);
			progress.setComputationState(ComputationState.FINISHED_OK);
			return;
		}

		// create the result graph
		final IGenlabGraph outGraph = inGraph.clone("outOfChains"); 
		final boolean shouldBeDirected = outGraph.getDirectionality() == GraphDirectionality.DIRECTED;
		
		// initialize random number generator
		RandomEngine coltRandom;
		if (seed != null) {
			if (seed > Integer.MAX_VALUE) {
				messages.warnUser("the seed provided is too long for the colt random generator; it will be truncated ", getClass());
			}
			coltRandom = new MersenneTwister(seed.intValue());			
		} else {
			coltRandom = new MersenneTwister();	
		}
		Uniform uniform = new Uniform(coltRandom);
		
		progress.incProgressMade();

		for (int chainIdx=0; chainIdx < count; chainIdx++) {
			
			String previousVertexId;
			
			// pickup a random node for anchoring
			if (inGraph.getVerticesCount() == 0) {
				// create the first node
				previousVertexId = outGraph.addVertex();
			} else {
				// choose one randomly
				int idxNode = uniform.nextIntFromTo(0, (int)inGraph.getVerticesCount());
				previousVertexId = inGraph.getVertex(idxNode);
			}
			
			for (int nodeIdx = 0; nodeIdx < length; nodeIdx++) {
		
				String newVertexId = outGraph.addVertex();	
				outGraph.addEdge(
						previousVertexId, 
						newVertexId, 
						shouldBeDirected?uniform.nextBoolean():false
						);
				previousVertexId = newVertexId;
			}
			
			progress.incProgressMade(length);
		}
	
		result.setResult(AddChainsAlgo.OUTPUT_GRAPH, outGraph);
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
