package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class SIIGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_islands_n = new IntegerInOut(
			"in_islands_n", 
			"islands_n", 
			"Number of islands to create",
			5
			);
	
	public static final IntegerInOut INPUT_islands_size = new IntegerInOut(
			"in_islands_size", 
			"islands_size", 
			"The size of each island.",
			20
			);
	
	public static final DoubleInOut INPUT_islands_pin = new DoubleInOut(
			"in_islands_pin", 
			"islands_pin", 
			"Probability to create links within islands.",
			0.2
			);
	

	public static final IntegerInOut INPUT_n_inter = new IntegerInOut(
			"in_n_inter", 
			"n_inter", 
			"Number of links between islands.",
			1
			);
	
	
	
	public SIIGeneratorAlgo() {
		super(
				"Simple Interconnected Islands(igraph)", 
				"Creates several islands, each being an Erdos Renyi G(n,p) network, interconnected with links."
				);
		
		inputs.add(INPUT_islands_n);
		inputs.add(INPUT_islands_size);
		inputs.add(INPUT_islands_pin);
		inputs.add(INPUT_n_inter);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractIGraphGeneratorExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 1000;
			}
			
			@Override
			protected IGraphGraph generateGraph(IGraphLibrary lib,
					ListOfMessages messages) {

				return lib.generateInterconnectedIslands(
						(Integer)getInputValueForInput(INPUT_islands_n), 
						(Integer)getInputValueForInput(INPUT_islands_size), 
						(Double)getInputValueForInput(INPUT_islands_pin), 
						(Integer)getInputValueForInput(INPUT_n_inter),
						true,
						true
						);
				
				
			}
		};
	}

}
