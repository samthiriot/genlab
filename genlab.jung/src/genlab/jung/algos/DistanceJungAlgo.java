package genlab.jung.algos;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class DistanceJungAlgo extends AbstractJungMeasureAlgo {

	
	public static final DoubleInOut OUTPUT_DIAMETER = new DoubleInOut(
			"out_diameter", 
			"diameter", 
			"the diameter"
			);
	
	public DistanceJungAlgo() {
		super(
				"distance (JUNG)", 
				"distances in the graph"
				);
		
		outputs.add(OUTPUT_DIAMETER);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractJungAlgoExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 2000;
			}
			
			@Override
			protected void compute(Graph<String, String> jungGraph,
					IGenlabGraph glGraph, ComputationResult results,
					IComputationProgress progress) {
				
				if (isUsed(OUTPUT_DIAMETER) || exec.getExecutionForced()) {
					double diam = DistanceStatistics.diameter(jungGraph); 
					results.setResult(OUTPUT_DIAMETER, diam);
				}

			}
		};
	}


}
