package genlab.core.model.meta.basics.graphs;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphReplay;

/**
 * TODO manage big graphs (optimized for very big graphs)
 * @author Samuel Thiriot
 *
 */
public class SimpleGraphstreamBasedGraph extends AbstractGraphstreamBasedGraph {

	final protected GraphDirectionality directionality;
	
	public SimpleGraphstreamBasedGraph(String graphId, GraphDirectionality directionality) {
		super(new SingleGraph(graphId, true, false));
		this.directionality = directionality;
	}

	@Override
	public boolean isMultiGraph() {
		return false;
	}

	@Override
	public GraphDirectionality getDirectionality() {
		return directionality;
	}

	@Override
	protected AbstractGraphstreamBasedGraph getGraphImplementationForClone(
			String cloneId) {
		return new SimpleGraphstreamBasedGraph(cloneId, directionality);
	}
	

}
