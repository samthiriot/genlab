package genlab.random.colt.algos.graph;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public abstract class AbstractAlgoRandomEdgeExec extends AbstractAlgoExecutionOneshot {

	public AbstractAlgoRandomEdgeExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}


	protected abstract IGenlabGraph computeGraph(IGenlabGraph inGraph, RandomEngine randomEngine);

	
	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		IGenlabGraph inGraph = (IGenlabGraph)getInputValueForInput(AbstractBasicAlgoRandomEdges.INPUT_GRAPH);
		
		
		RandomEngine coltRandom = new MersenneTwister();

		IGenlabGraph outGraph = computeGraph(inGraph, coltRandom);
		
		
		result.setResult(AbstractBasicAlgoRandomEdges.OUTPUT_GRAPH, outGraph);
		
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

}
