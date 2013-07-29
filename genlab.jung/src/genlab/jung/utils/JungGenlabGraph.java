package genlab.jung.utils;

import edu.uci.ics.jung.graph.util.EdgeType;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

import java.util.Collection;
import java.util.Iterator;

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
	public boolean addVertex(String arg0) {
		try {
			glGraph.addVertex(arg0);
			return true;
		} catch (WrongParametersException e) {
			return false;
		}
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
		try {
			glGraph.addEdge(arg0, arg1, arg2);
			return true;
		} catch (WrongParametersException e) {
			return false;
		}
	}

	@Override
	public boolean addEdge(String arg0, String arg1, String arg2, EdgeType arg3) {
		try {
			glGraph.addEdge(arg0, arg1, arg2, arg3==EdgeType.DIRECTED);
			return true;
		} catch (WrongParametersException e) {
			return false;
		}
	}

	@Override
	public boolean addEdge(String arg0, Collection<? extends String> arg1) {
		
		if (arg1.size() != 2)
			throw new WrongParametersException("wrong number of vertices");
		
		Iterator<? extends String> it = arg1.iterator();
		String idFrom = it.next();
		String idTo = it.next();
		
		try {
			glGraph.addEdge(arg0, idFrom, idTo);
		
			return true;
		} catch (WrongParametersException e) {
			return false;
		}
	}

}
