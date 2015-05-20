package genlab.igraph.algos.measure;

import java.util.Arrays;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestNodeBetweenessEstimates extends AbstractTestMeasure<double[]> {

	public TestNodeBetweenessEstimates(IGraphLibImplementation lib) {
		super(lib);

	}

	@Override
	protected double[] applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		return lib.computeNodeBetweenessEstimate(
				graph, 
				graph.getDirectionality()==GraphDirectionality.DIRECTED, 
				0.1, 
				exec
				);
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
