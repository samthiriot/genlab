package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.BooleanInOut;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;

public class BarabasiAlbertGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_N = new IntegerInOut(
			"in_N", 
			"N", 
			"Number of vertices to create",
			200
			);
	
	public static final IntegerInOut INPUT_M = new IntegerInOut(
			"in_M", 
			"M", 
			"number of nodes to add at each step",
			1
			);
	
	public static final DoubleInOut INPUT_POWER = new DoubleInOut(
			"in_power", 
			"power", 
			"The power of the preferential attachment",
			1.0
			);
	
	public static final DoubleInOut INPUT_ZERO_APPEAL = new DoubleInOut(
			"in_zeroappeal", 
			"zero appeal", 
			"The attractiveness of the vertices with no adjacent edges",
			1.0
			);
	
	public static final BooleanInOut INPUT_OUTPUT_PREF = new BooleanInOut(
			"param_output_pref", 
			"output pref", 
			"if true not only the in- but also the out-degree of a vertex increases its citation probability", 
			false
			);
	
	public static final DoubleInOut INPUT_A = new DoubleInOut(
			"in_a", 
			"A", 
			"The probability that a vertex is cited is proportional to d^power+A",
			1.0
			);
	
	public static final BooleanParameter PARAM_DIRECTED = new BooleanParameter(
			"param_directed", 
			"directed", 
			"generate directed graphs", 
			false
			);
	
	public static final BooleanParameter PARAM_LOOPS = new BooleanParameter(
			"param_loops", 
			"loops", 
			"allow for loops in generated graphs", 
			true
			);
	
	
	public BarabasiAlbertGeneratorAlgo() {
		super(
				"Barabasi Albert preferential attachement (igraph)", 
				"Generates a scale-free graph using preferential attachement as encoded in igraph"
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_M);
		inputs.add(INPUT_POWER);
		inputs.add(INPUT_ZERO_APPEAL);
		inputs.add(INPUT_OUTPUT_PREF);
		inputs.add(INPUT_A);

		
		registerParameter(PARAM_DIRECTED);
		registerParameter(PARAM_LOOPS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new BarabasiAlbertGeneratorExec(execution, algoInstance);
	}

}
