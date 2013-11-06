package genlab.cytoscape.plugin.randomnetworks.analysis;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import cytoscape.randomnetwork.MeanShortestPathMetric;
import cytoscape.randomnetwork.RandomNetwork;

public class MeanShortestPathAlgo extends RandomNetworkAnalyzerAlgo {

	public static final DoubleInOut OUTPUT_MEANPATH = new DoubleInOut("out_mean_shortestpath", "mean shortest path", "the mean of the shortests paths in the graph");
	
	public MeanShortestPathAlgo() {
		super(
				"average path length (cytoscape)", 
				"computes the mean shortest path"
				);
		 outputs.add(OUTPUT_MEANPATH);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new RandomNetworkAnalyzeExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 1000;
			}
			
			@Override
			protected void analyze(RandomNetwork cyNetwork, boolean directed) {
				
				MeanShortestPathMetric metric = new MeanShortestPathMetric();
				
				double meanPath = metric.analyze(cyNetwork, directed);
				
				((ComputationResult)getResult()).setResult(
						OUTPUT_MEANPATH, 
						meanPath
						);
				
			}
		};
	}

}
