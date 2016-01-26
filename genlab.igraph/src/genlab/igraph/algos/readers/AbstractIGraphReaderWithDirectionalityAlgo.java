package genlab.igraph.algos.readers;

import genlab.core.parameters.BooleanParameter;

public abstract class AbstractIGraphReaderWithDirectionalityAlgo extends
		AbstractIGraphReaderAlgo {

	public static final BooleanParameter PARAM_DIRECTED = new BooleanParameter(
			"param_directed", 
			"directed", 
			"read the graph as being directed", 
			false
			);
	
	public AbstractIGraphReaderWithDirectionalityAlgo(String name,
			String description) {
		super(name, description);
		
		registerParameter(PARAM_DIRECTED);
	}


}
