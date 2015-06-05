package genlab.jung.utils;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

/**
 * Returns a JUNG graph for a genlab graph. In practice this is the very same graph, without change.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class Converters {


	public static Graph<String, String> getJungGraphForGenlabGraphReadonly(IGenlabGraph g) {
		return new JungGenlabGraphReadonly(g);
	}
	
	public static Graph<String, String> getJungGraphForGenlabGraphWritable(IGenlabGraph g) {
		return new JungGenlabGraph(g);
	}
	
	public static IGenlabGraph getGenlabGraphFromJUNG(Graph<String, String> jungGraph) {

		IGenlabGraph glGraph = GraphFactory.createGraph("generated", GraphDirectionality.UNDIRECTED, false);
		
		// add nodes
		for (String vertexId : jungGraph.getVertices()) {
			
			glGraph.addVertex(vertexId);
			
		}
		
		// add edges
		for (String edgeId : jungGraph.getEdges()) {
			
			glGraph.addEdge(
					edgeId, 
					jungGraph.getSource(edgeId), 
					jungGraph.getDest(edgeId)
					);
			
		}
		
		return glGraph;
	}
	
	private Converters() {
		
	}

}
