package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class WattsStrogatzGeneratorExec extends AbstractIGraphGeneratorExec {
	
	public WattsStrogatzGeneratorExec(IExecution execution, AlgoInstance algoInstanc)  {
		super(execution, algoInstanc);
	}	
	
	public WattsStrogatzGeneratorExec()  {}		
		
		
	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {

		
		int N = (Integer)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_N);
		int nei = (Integer)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_NEI);
		double p= (Double)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_P);

		if (nei < 0)
			throw new WrongParametersException("nei should be > 0");
		
		boolean allowLoops = (Boolean)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_ALLOW_LOOPS.getId());
		boolean directed = (Boolean)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_DIRECTED.getId());
		int dim = (Integer)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_DIM.getId());
								
		return lib.generateWattsStrogatz(N, dim, p, nei, directed, allowLoops);
		
	}


}
