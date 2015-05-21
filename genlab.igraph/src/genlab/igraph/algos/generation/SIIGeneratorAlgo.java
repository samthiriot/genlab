package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.flowtypes.ProbabilityInOut;

public class SIIGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_islands_n = new IntegerInOut(
			"in_islands_n", 
			"islands_n", 
			"Number of islands to create",
			5,
			1
			);
	
	public static final IntegerInOut INPUT_islands_size = new IntegerInOut(
			"in_islands_size", 
			"islands_size", 
			"The size of each island.",
			20,
			0
			);
	
	public static final ProbabilityInOut INPUT_islands_pin = new ProbabilityInOut(
			"in_islands_pin", 
			"islands_pin", 
			"Probability to create links within islands.",
			0.2
			);
	

	public static final IntegerInOut INPUT_n_inter = new IntegerInOut(
			"in_n_inter", 
			"n_inter", 
			"Number of links between islands.",
			1,
			0
			);
	
	
	
	public SIIGeneratorAlgo() {
		super(
				"Simple Interconnected Islands(igraph)", 
				"Creates several islands, each being an Erdos Renyi G(n,p) network, interconnected with links.",
				true
				);
		
		inputs.add(INPUT_islands_n);
		inputs.add(INPUT_islands_size);
		inputs.add(INPUT_islands_pin);
		inputs.add(INPUT_n_inter);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new SIIGeneratorExec(execution, algoInstance);
	}

}
