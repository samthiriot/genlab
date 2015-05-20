package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestComponentCount extends AbstractTestMeasure<Integer> {

	public TestComponentCount(IGraphLibImplementation lib) {
		super(lib);

	}
	
	

	@Override
	protected IGenlabGraph generateTestGraph(Long seed, IExecution exec) {
		// we prefer here to generate a random disconnected graph
		return lib.generateErdosRenyiGNP(100, 0.01, false, false, exec, seed);
	}


	@Override
	protected Integer applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		return lib.computeComponentsCount(graph, exec);
	}

	@Override
	protected boolean checkMeasuresEqual(Integer m1, Integer m2) {
		return m1==m2;
	}

	@Override
	protected boolean checkMeasuresDifferent(Integer m1, Integer m2) {
		return m1!=m2;
	}

}
