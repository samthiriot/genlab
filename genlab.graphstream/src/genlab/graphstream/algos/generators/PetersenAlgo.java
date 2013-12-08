package genlab.graphstream.algos.generators;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;
import org.graphstream.algorithm.generator.PetersenGraphGenerator;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class PetersenAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Petersen graph (graphstream)";
		
	
	
	public PetersenAlgo() {
		super(
				ALGO_NAME,
				"Generate a Petersen graph. In the mathematical field of graph theory, the Petersen graph is an undirected graph with 10 vertices and 15 edges",
				ExistingAlgoCategories.STATIC_GRAPHS
				);
		
	}
		


	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		return new PetersenGraphGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}

	


}
