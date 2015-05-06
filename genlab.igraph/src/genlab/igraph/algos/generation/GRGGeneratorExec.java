package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class GRGGeneratorExec extends AbstractIGraphGeneratorExec {

	public GRGGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public GRGGeneratorExec() {

	}
	
	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {

		int nodes = (Integer)getInputValueForInput(GRGGeneratorAlgo.INPUT_NODES);
		double radius = (Double)getInputValueForInput(GRGGeneratorAlgo.INPUT_RADIUS);

		boolean torus = (Boolean)algoInst.getValueForParameter(GRGGeneratorAlgo.PARAM_TORUS.getId());

		return lib.generateGRG(nodes, radius, torus);
		
	}
}
