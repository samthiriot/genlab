package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class BarabasiAlbertGeneratorExec extends AbstractIGraphGeneratorExec {

	public BarabasiAlbertGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public BarabasiAlbertGeneratorExec() {
	}


	@Override
	public long getTimeout() {
		return 1000;
	}
				
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {

		
		return lib.generateBarabasiAlbert(
				(Integer)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_N), 
				(Integer)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_M), 
				(Double)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_POWER),
				(Double)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_ZERO_APPEAL),
				(Boolean)algoInst.getValueForParameter(BarabasiAlbertGeneratorAlgo.PARAM_DIRECTED.getId()),
				(Boolean)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_OUTPUT_PREF),
				(Double)getInputValueForInput(BarabasiAlbertGeneratorAlgo.INPUT_A)
				);
		
	}
	
	

}
