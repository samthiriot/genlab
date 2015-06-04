package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

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
	protected IGenlabGraph generateGraph() {

		
		int N = (Integer)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_N);
		int nei = (Integer)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_NEI);
		double p= (Double)getInputValueForInput(WattsStrogatzGeneratorAlgo.INPUT_P);

		if (nei < 0)
			throw new WrongParametersException("nei should be > 0");
		
		boolean allowLoops = (Boolean)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_ALLOW_LOOPS.getId());
		boolean allowMultiple = (Boolean)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_ALLOW_MULTIPLE.getId());
		int dim = (Integer)algoInst.getValueForParameter(WattsStrogatzGeneratorAlgo.PARAM_DIM.getId());
								
		Long seed = (Long)algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_SEED);

		return IgraphLibFactory.getImplementation((Integer) algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_IMPLEMENTATION))
				.generateWattsStrogatz(N, dim, p, nei, allowLoops, allowMultiple, this.exec, seed);
		
	}


}
