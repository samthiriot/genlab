package genlab.jung.utils;

import edu.uci.ics.jung.graph.util.EdgeType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

/**
 * A JUNG graph implementation which is based on the genlab graph
 * 
 * @author Samuel Thiriot
 *
 */
public final class JungGenlabGraph extends JungGenlabGraphReadonly {

	
	public JungGenlabGraph(IGenlabGraph glGraph) {

		super(glGraph);
	}


	@Override
	public boolean removeEdge(String arg0) {
		return glGraph.removeEdge(arg0);
	}

	@Override
	public boolean removeVertex(String arg0) {
		return glGraph.removeVertex(arg0);
	}

	@Override
	public boolean addEdge(String arg0, String arg1, String arg2) {
		glGraph.addEdge(arg0, arg1, arg2);
		return true;
	}

	@Override
	public boolean addEdge(String arg0, String arg1, String arg2, EdgeType arg3) {
		glGraph.addEdge(arg0, arg1, arg2, arg3==EdgeType.DIRECTED);
		return true;
	}


}
