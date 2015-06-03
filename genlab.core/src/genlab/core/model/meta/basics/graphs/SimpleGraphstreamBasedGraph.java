package genlab.core.model.meta.basics.graphs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.graphstream.graph.implementations.SingleGraph;

/**
 * TODO manage big graphs (optimized for very big graphs)
 * @author Samuel Thiriot
 *
 */
@SuppressWarnings("serial")
public class SimpleGraphstreamBasedGraph extends AbstractGraphstreamBasedGraph implements Externalizable {

	protected GraphDirectionality directionality;
	
	protected final Collection<String> linktypes = Collections.unmodifiableCollection(new LinkedList<String>() {{ add("edges"); }});
	
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


	public SimpleGraphstreamBasedGraph() {
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

	@Override
	public final Collection<String> getEdgeTypes() {
		return linktypes;
	}

	@Override
	public final String getEdgeType(String edgeId) {
		return "edges";
	}
	

}
