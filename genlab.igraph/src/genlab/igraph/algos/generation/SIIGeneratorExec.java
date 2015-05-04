package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class SIIGeneratorExec extends AbstractIGraphGeneratorExec {

	public SIIGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public SIIGeneratorExec() {
	}

	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {

		return lib.generateInterconnectedIslands(
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_n), 
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_size), 
				(Double)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_pin), 
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_n_inter),
				true,
				true
				);
		
		
	}

}
