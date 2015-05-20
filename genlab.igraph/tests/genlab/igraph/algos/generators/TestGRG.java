package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestGRG extends AbstractTestGenerator {

	public TestGRG(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.UNDIRECTED, 
				1000,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateGRG(1000, 0.05, true, exec, seed);
	}

}
