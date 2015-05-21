package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.flowtypes.ProbabilityInOut;
import genlab.core.parameters.BooleanParameter;

public class ErdosRenyiGNPGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_N = new IntegerInOut(
			"in_N", 
			"N", 
			"Number of vertices to create",
			200,
			0
			);
	
	public static final ProbabilityInOut INPUT_P = new ProbabilityInOut(
			"in_P", 
			"P", 
			"The probability to create a link.",
			0.2
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
	
	
	public ErdosRenyiGNPGeneratorAlgo() {
		super(
				"Erdos Reny G(n,p) (igraph)", 
				"Generates a random (Erdos-Renyi) graph",
				true
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_P);
		
		registerParameter(PARAM_DIRECTED);
		registerParameter(PARAM_LOOPS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new ErdosRenyiGNPGeneratorExec(execution, algoInstance);
	}

}
