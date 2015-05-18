package genlab.igraph.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestERGNPDirected extends AbstractTestGenerator {

	public TestERGNPDirected(IGraphLibImplementation lib) {
		super(
				lib, 
				GraphDirectionality.DIRECTED, 
				500,
				null);
	}

	@Override
	protected IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec) {
		return lib.generateErdosRenyiGNP(500, 0.1, true, false, exec, seed);
	}

}
