package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

import java.util.Arrays;

public class TestNodeAlphaCentrality extends AbstractTestMeasure<double[]> {

	public TestNodeAlphaCentrality(IGraphLibImplementation lib) {
		super(lib);

	}

	@Override
	protected double[] applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		return lib.computeNodeAlphaCentrality(graph, exec);
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
