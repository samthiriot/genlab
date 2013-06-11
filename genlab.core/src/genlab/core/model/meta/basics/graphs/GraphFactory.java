package genlab.core.model.meta.basics.graphs;

public class GraphFactory {

	public static IGenlabGraph createGraph(String graphId, GraphDirectionality directionality, boolean multiplex) {
		if (multiplex)
			return new MultiplexGraphstreamBasedGraph(graphId, directionality);
		else 
			return new SimpleGraphstreamBasedGraph(graphId, directionality);
	}
	
}
