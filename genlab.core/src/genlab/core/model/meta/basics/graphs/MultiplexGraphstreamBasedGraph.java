package genlab.core.model.meta.basics.graphs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.graphstream.graph.implementations.MultiGraph;

/**
 * TODO manage big graphs (optimized for very big graphs)
 * @author Samuel Thiriot
 *
 */
@SuppressWarnings("serial")
public class MultiplexGraphstreamBasedGraph extends AbstractGraphstreamBasedGraph implements Externalizable {

	protected GraphDirectionality directionality;
	
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


	public MultiplexGraphstreamBasedGraph() {
		super();
		directionality = GraphDirectionality.DIRECTED;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(directionality);
		
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		directionality = (GraphDirectionality) in.readObject();

	}


	
}
