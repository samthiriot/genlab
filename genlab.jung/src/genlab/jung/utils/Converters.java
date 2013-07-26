package genlab.jung.utils;

import edu.uci.ics.jung.graph.Graph;
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
	
	private Converters() {
		
	}

}
