package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class ErdosRenyiGNPGeneratorExec extends AbstractIGraphGeneratorExec {

	public ErdosRenyiGNPGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ErdosRenyiGNPGeneratorExec() {
	}
	
	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {

		int nodes = (Integer)getInputValueForInput(ErdosRenyiGNPGeneratorAlgo.INPUT_N);
		double proba = (Double)getInputValueForInput(ErdosRenyiGNPGeneratorAlgo.INPUT_P);

		
		boolean directed = (Boolean)algoInst.getValueForParameter(ErdosRenyiGNPGeneratorAlgo.PARAM_DIRECTED.getId());
		boolean loops = (Boolean)algoInst.getValueForParameter(ErdosRenyiGNPGeneratorAlgo.PARAM_LOOPS.getId());

		return lib.generateErdosRenyiGNP(nodes, proba, directed, loops);
		
	}
	
}
