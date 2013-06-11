package genlab.gephi.algos.measure;

import java.util.HashMap;
import java.util.Map;

import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.gephi.utils.GephiConvertors;

/**
 * @see http://wiki.gephi.org/index.php/Avg_Path_Length
 * @see http://gephi.org/docs/toolkit/org/gephi/statistics/plugin/GraphDistance.html
 * 
 * @author Samuel Thiriot
 *
 */
public class GephiAveragePathLengthAlgo extends GephiAbstractAlgo {

	public GephiAveragePathLengthAlgo() {
		super(
				"Gephi Brandes", 
				"computes the average path length"
				);
	}
	

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GephiAbstractAlgoExecution(execution, algoInstance) {
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, 
					Workspace gephiWorkspace,
					IGenlabGraph genlabGraph) {
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				GraphDistance algo = new GraphDistance();
				algo.setDirected(genlabGraph.getDirectionality() == GraphDirectionality.DIRECTED);
				
				// TODO warning if mixed ???
				
				// TODO progress
				
				algo.execute(
						GephiConvertors.getGraphModel(gephiWorkspace),
						GephiConvertors.getAttributeModel(gephiWorkspace)
						);

				System.err.println("average path length: "+algo.getPathLength());
				System.err.println("diameter: "+algo.getDiameter());
				// TODO process results
				
				/*AttributeColumn col = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
				 
				//Iterate over values
				for (Node n : graph.getNodes()) {
				   Double centrality = (Double)n.getNodeData().getAttributes().getValue(col.getIndex());
				}
				*/
				return results;
				
			}

		};
	}

}
