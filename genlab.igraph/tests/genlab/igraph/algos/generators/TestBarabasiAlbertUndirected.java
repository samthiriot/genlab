package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestBarabasiAlbertUndirected extends AbstractTestGenerator {

	public TestBarabasiAlbertUndirected(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.UNDIRECTED, 
				1000,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateBarabasiAlbert(1000, 2, 0.12, 1.23, false, false, 1, exec, seed);
	}

}
