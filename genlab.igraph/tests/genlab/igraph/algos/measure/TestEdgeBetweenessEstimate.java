package genlab.igraph.algos.measure;

import java.util.Arrays;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraphLibImplementation;

public class TestEdgeBetweenessEstimate extends AbstractTestMeasure<double[]> {

	public TestEdgeBetweenessEstimate(IGraphLibImplementation lib) {
		super(lib);

	}

	@Override
	protected double[] applyMeasure(IGenlabGraph graph,
			IGraphLibImplementation lib, IExecution exec) {
		
		return lib.computeEdgeBetweeness(graph, graph.getDirectionality()==GraphDirectionality.DIRECTED, exec);
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
