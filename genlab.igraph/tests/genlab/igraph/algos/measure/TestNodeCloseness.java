package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

import java.util.Arrays;

public class TestNodeCloseness extends AbstractTestMeasure<double[]> {

	public TestNodeCloseness(IGraphLibImplementation lib) {
		super(lib);

	}

	@Override
	protected double[] applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		return lib.computeNodeCloseness(graph, exec);
	}

	@Override
	protected boolean checkMeasuresEqual(double[] m1, double[] m2) {
		return Arrays.equals(m1, m2);
	}

	@Override
	protected boolean checkMeasuresDifferent(double[] m1, double[] m2) {
		return !Arrays.equals(m1, m2);
	}

}
