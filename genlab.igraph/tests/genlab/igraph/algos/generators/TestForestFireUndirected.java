package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestForestFireUndirected extends AbstractTestGenerator {

	public TestForestFireUndirected(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.DIRECTED, 
				1000,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateForestFire(1000, 0.15, 0.2, 2, true, true, true, exec, seed);
	}

}
