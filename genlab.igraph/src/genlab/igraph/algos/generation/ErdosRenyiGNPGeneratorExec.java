package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphNativeLibrary;

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
	protected IGenlabGraph generateGraph() {
				
		int nodes = (Integer)getInputValueForInput(ErdosRenyiGNPGeneratorAlgo.INPUT_N);
		double proba = (Double)getInputValueForInput(ErdosRenyiGNPGeneratorAlgo.INPUT_P);

		
		boolean directed = (Boolean)algoInst.getValueForParameter(ErdosRenyiGNPGeneratorAlgo.PARAM_DIRECTED.getId());
		boolean loops = (Boolean)algoInst.getValueForParameter(ErdosRenyiGNPGeneratorAlgo.PARAM_LOOPS.getId());

		Long seed = (Long)algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_SEED);

		return IgraphLibFactory.getImplementation().generateErdosRenyiGNP(nodes, proba, directed, loops, this.exec, seed);
		
	}
	
}
