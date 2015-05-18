package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestSSI extends AbstractTestGenerator {

	public TestSSI(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.UNDIRECTED, 
				10*50,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateInterconnectedIslands(10, 50, 0.1, 1, false, false, exec, seed);
	}

}
