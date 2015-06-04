package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphNativeLibrary;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

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
	protected IGenlabGraph generateGraph() {

		return IgraphLibFactory.getImplementation((Integer) algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_IMPLEMENTATION))
				.generateInterconnectedIslands(
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_n), 
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_size), 
				(Double)getInputValueForInput(SIIGeneratorAlgo.INPUT_islands_pin), 
				(Integer)getInputValueForInput(SIIGeneratorAlgo.INPUT_n_inter),
				true,
				true,
				this.exec,
				(Long)algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_SEED)
				);
		
		
	}

}
