package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class ErdosRenyiGNMGeneratorExec extends AbstractIGraphGeneratorExec {

	public ErdosRenyiGNMGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ErdosRenyiGNMGeneratorExec() {
	}
	
	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGenlabGraph generateGraph() {

		return IgraphLibFactory.getImplementation((Integer) algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_IMPLEMENTATION))
				.generateErdosRenyiGNM(
				(Integer)getInputValueForInput(ErdosRenyiGNMGeneratorAlgo.INPUT_N), 
				(Double)getInputValueForInput(ErdosRenyiGNMGeneratorAlgo.INPUT_M), 
				(Boolean)algoInst.getValueForParameter(ErdosRenyiGNMGeneratorAlgo.PARAM_DIRECTED.getId()), 
				(Boolean)algoInst.getValueForParameter(ErdosRenyiGNMGeneratorAlgo.PARAM_LOOPS.getId()),
				this.exec,
				(Long)algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_SEED)
				);
		
	}
}
