package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class ErdosRenyiGNMGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_N = new IntegerInOut(
			"in_N", 
			"N", 
			"Number of vertices to create",
			200
			);
	
	public static final DoubleInOut INPUT_M = new DoubleInOut(
			"in_M", 
			"m", 
			"number of edges to create",
			400.0
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
	
	
	public ErdosRenyiGNMGeneratorAlgo() {
		super(
				"Erdos Reny G(n,m) (igraph)", 
				"Generates a random (Erdos-Renyi) graph"
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_M);
		
		registerParameter(PARAM_DIRECTED);
		registerParameter(PARAM_LOOPS);
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

				return lib.generateErdosRenyiGNM(
						(Integer)getInputValueForInput(INPUT_N), 
						(Double)getInputValueForInput(INPUT_M), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId()), 
						(Boolean)algoInst.getValueForParameter(PARAM_LOOPS.getId())
						);
				
			}
		};
	}

}
