package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;

public class GRGGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_NODES = new IntegerInOut(
			"in_nodes", 
			"nodes", 
			"number of vertices to create",
			200,
			0
			);
	
	public static final DoubleInOut INPUT_RADIUS = new DoubleInOut(
			"in_radius", 
			"radius", 
			"The radius within which the vertices will be connected.",
			0.1,
			0.0
			);
	
	public static final BooleanParameter PARAM_TORUS = new BooleanParameter(
			"param_torus", 
			"torus", 
			"if true periodic boundary conditions will be used, ie. the vertices are assumed to be on a torus instead of a square.", 
			false
			);
	
	
	
	public GRGGeneratorAlgo() {
		super(
				"euclidian generator (igraph)", 
				"A geometric random graph is created by dropping points (=vertices) randomly to the unit square and then connecting all those pairs which are less than radius apart in Euclidean norm.",
				true
				);
		
		inputs.add(INPUT_NODES);
		inputs.add(INPUT_RADIUS);
		
		registerParameter(PARAM_TORUS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GRGGeneratorExec(execution, algoInstance);
		
	}

}
