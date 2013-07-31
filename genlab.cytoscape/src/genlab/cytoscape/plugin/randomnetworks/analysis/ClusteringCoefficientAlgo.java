package genlab.cytoscape.plugin.randomnetworks.analysis;

import cytoscape.randomnetwork.ClusteringCoefficientMetric;
import cytoscape.randomnetwork.RandomNetwork;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

public class ClusteringCoefficientAlgo extends RandomNetworkAnalyzerAlgo {

	public static final DoubleInOut OUTPUT_AVERAGE_CLUSTERING = new DoubleInOut("out_average_clustering", "average clustering", "the average of the clustering of each node");
	
	public ClusteringCoefficientAlgo() {
		super(
				"clustering coefficient (cytoscape)", 
				"computes the average clustering coefficient"
				);
		 outputs.add(OUTPUT_AVERAGE_CLUSTERING);
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
				
				ClusteringCoefficientMetric metric = new ClusteringCoefficientMetric();
				double clusteringCoef = metric.analyze(cyNetwork, directed);
				
				((ComputationResult)getResult()).setResult(
						OUTPUT_AVERAGE_CLUSTERING, 
						clusteringCoef
						);
				
			}
		};
	}

}
