package genlab.graphstream.algos.generators;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;

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

public class ChvatalAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Chvatal graph (graphstream)";
		
	
	
	public ChvatalAlgo() {
		super(
				ALGO_NAME,
				"Generate the Chvatal graph.  In the mathematical field of graph theory, the Chvátal graph is an undirected graph with 12 vertices and 24 edges, discovered by Václav Chvátal (1970). It is triangle-free: its girth (the length of its shortest cycle) is four. It is 4-regular: each vertex has exactly four neighbors. And its chromatic number is 4: it can be colored using four colors, but not using only three. It is, as Chvátal observes, the smallest possible 4-chromatic 4-regular triangle-free graph; the only smaller 4-chromatic triangle-free graph is the Grötzsch graph, which has 11 vertices but is not regular. ",
				ExistingAlgoCategories.STATIC_GRAPHS.getTotalId()
				);
		
	}
		


	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		
		return new ChvatalGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}

	


}
