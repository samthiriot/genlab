package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.GraphBasicPropertiesExec;

public class GraphBasicPropertiesAlgo extends BasicAlgo {


	public static final GraphInOut INPUT_GRAPH = new GraphInOut(
			"in_graph", 
			"graph", 
			"graph to analyze"
			);
	
	public static final IntegerInOut OUTPUT_COUNT_VERTICES = new IntegerInOut(
			"out_count_vertices", 
			"num vertices", 
			"number of vertices in the graph"
			);

	public static final IntegerInOut OUTPUT_COUNT_EDGES = new IntegerInOut(
			"out_count_edges", 
			"num edges", 
			"number of edges in the graph"
			);
	
	public static final DoubleInOut OUTPUT_DENSITY = new DoubleInOut(
			"out_density", 
			"density", 
			"density in the graph"
			);
	
	public static final DoubleInOut OUTPUT_AVERAGE_DEGREE = new DoubleInOut(
			"out_avdegree", 
			"average degree", 
			"average degree"
			);
	
	/*
	public static final BooleanInOut OUTPUT_DIRECTED = new DoubleInOut(
			"out_density", 
			"density", 
			"density in the graph"
			);
	*/
	
	
	public GraphBasicPropertiesAlgo() {
		super(
				"basic properties", 
				"basic graph properties", 
				ExistingAlgoCategories.ANALYSIS_GRAPH, 
				null,
				null
				);
		
		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_COUNT_EDGES);
		outputs.add(OUTPUT_COUNT_VERTICES);
		outputs.add(OUTPUT_DENSITY);
		outputs.add(OUTPUT_AVERAGE_DEGREE);
	}

	
	@Override
	public IAlgoExecution createExec(
			final IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new GraphBasicPropertiesExec(execution, algoInstance);
		
	}

}
