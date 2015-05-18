package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestAveragePathLength extends AbstractTestMeasure<Double> {

	public TestAveragePathLength(IGraphLibImplementation lib) {
		super(lib);

	}

	@Override
	protected Double applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		return lib.computeAveragePathLength(graph, exec);
	}

	@Override
	protected boolean checkMeasuresEqual(Double m1, Double m2) {
		return Math.abs(m1-m2) < Double.MIN_VALUE;
	}

	@Override
	protected boolean checkMeasuresDifferent(Double m1, Double m2) {
		return Math.abs(m1-m2) > Double.MIN_VALUE;
	}

}
