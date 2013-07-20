package genlab.core.model.meta.basics.graphs;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.GraphReplay;

/**
 * TODO manage big graphs (optimized for very big graphs)
 * @author Samuel Thiriot
 *
 */
public class MultiplexGraphstreamBasedGraph extends AbstractGraphstreamBasedGraph {

	final protected GraphDirectionality directionality;
	
	public MultiplexGraphstreamBasedGraph(String graphId, GraphDirectionality directionality) {
		super(new MultiGraph(graphId, true, false));
		this.directionality = directionality;
	}

	@Override
	public boolean isMultiGraph() {
		return true;
	}

	@Override
	public GraphDirectionality getDirectionality() {
		return directionality;
	}

	@Override
	protected AbstractGraphstreamBasedGraph getGraphImplementationForClone(
			String cloneId) {
		return new MultiplexGraphstreamBasedGraph(cloneId, directionality);
	}


	

	
}
