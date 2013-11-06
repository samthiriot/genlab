package genlab.gephi.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gephi.utils.GephiConvertors;
import genlab.gephi.utils.GephiGraph;
import genlab.gephi.utils.ProgressTicketGephiToGenlab;

import java.util.HashMap;
import java.util.Map;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

/**
 * @see http://wiki.gephi.org/index.php/Avg_Path_Length
 * @see http://gephi.org/docs/toolkit/org/gephi/statistics/plugin/GraphDistance.html
 * 
 * @author Samuel Thiriot
 *
 */
public class GephiClusteringAlgo extends GephiAbstractAlgo {

	
	public static final InputOutput<Double> OUTPUT_AVERAGE_CLUSTERING_COEF = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_average_clustering_coef", 
			"average clustering coef", 
			"the average clustering coefficient"
	);
	

	
	public GephiClusteringAlgo() {
		super(
				"clustering (gephi)", 
				"computes the clustering coefficient",
				null
				);
		
		outputs.add(OUTPUT_AVERAGE_CLUSTERING_COEF);

	}
	

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GephiAbstractAlgoExecution(execution, algoInstance) {
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, 
					GephiGraph gephiGraph,
					IGenlabGraph genlabGraph) {
				
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				ClusteringCoefficient algo = null;
				for (int i=0; i<3; i++) {
					try {
						algo = new ClusteringCoefficient();
						break;
					} catch (NullPointerException e) {
						
					}
				}
				algo.setDirected(genlabGraph.getDirectionality() == GraphDirectionality.DIRECTED);
				
				// TODO warning if mixed ???
				
				// plug a progress ticket
				algo.setProgressTicket(new ProgressTicketGephiToGenlab(progress));
			
				algo.execute(
						gephiGraph.graphModel,
						gephiGraph.attributeModel
						);

				//GLLogger.debugTech("report from gephi "+algo.getReport(), getClass());
				
				
				results.put(OUTPUT_AVERAGE_CLUSTERING_COEF, algo.getAverageClusteringCoefficient());
				
				
				return results;
				
			}

			@Override
			public long getTimeout() {
				return 1000*60*10; // TODO timeout ? should be closed in 
			}


		};
	}

}
