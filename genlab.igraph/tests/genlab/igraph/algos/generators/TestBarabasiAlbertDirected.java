package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestBarabasiAlbertDirected extends AbstractTestGenerator {

	public TestBarabasiAlbertDirected(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.DIRECTED, 
				500,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateBarabasiAlbert(500, 2, 0.12, 1.23, true, false, 1, exec, seed);
	}

}
