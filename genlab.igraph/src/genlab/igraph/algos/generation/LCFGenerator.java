package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class LCFGenerator extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_VERTICES = new IntegerInOut(
			"in_vertices", 
			"N", 
			"number of vertices to create",
			200
			);
	
	public static final LCFParameter PARAM_LCF = new LCFParameter(
			"in_lcf", 
			"LCF", 
			"LCF in LCF notation",
			new LCF(new int[]{ 2,-2}, 2) // "[2,-2]2"
			);
	
			
	public LCFGenerator() {
		super(
				"LCF generation (igraph", 
				"LCF is short for Lederberg-Coxeter-Frucht, it is a concise notation for 3-regular Hamiltonian graphs. It consists of three parameters: the number of vertices in the graph, a list of shifts giving additional edges to a cycle backbone, and another integer giving how many times the shifts should be performed."
				);
		
		inputs.add(INPUT_VERTICES);
		
		registerParameter(PARAM_LCF);
		
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
				
				int count = (Integer)getInputValueForInput(INPUT_VERTICES);
				
				LCF lcf = (LCF)algoInst.getValueForParameter(PARAM_LCF.getId());
				
				return lib.generateLCF(count, lcf.shifts, lcf.count);

			}
		};
	}

}
