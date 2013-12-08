package genlab.random.colt.algos.graph;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

/**
 * TODO parameter random
 * TODO param add double
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractBasicAlgoRandomEdges extends BasicAlgo {

	public static GraphInOut INPUT_GRAPH = new GraphInOut(
			"in_graph", 
			"graph", 
			"the graph to update"
			);
	
	public static GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"the graph updated"
			);
			
	public AbstractBasicAlgoRandomEdges(String name, String desc) {
		super(
				name, 
				desc, 
				ExistingAlgoCategories.NOISE_GRAPH, 
				null, 
				null
				);
		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_GRAPH);
	}
	

	

}
