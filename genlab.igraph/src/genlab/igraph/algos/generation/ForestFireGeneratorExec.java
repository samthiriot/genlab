package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class ForestFireGeneratorExec extends AbstractIGraphGeneratorExec {
		
	public ForestFireGeneratorExec() {}

	public ForestFireGeneratorExec(IExecution execution,
			AlgoInstance algoInstance) {
		super(execution, algoInstance);
	}

	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGenlabGraph generateGraph() {
	
		Integer N = (Integer)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_N);
		//System.err.println("N "+N);
		Double fwProb = (Double)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_fw_prob);
		if (fwProb == 0.0)
			throw new WrongParametersException(ForestFireGeneratorAlgo.INPUT_fw_prob+" should be > 0");
			
		//System.err.println("fw "+fwProb);

		Double bwFactor = (Double)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_bw_factor);
		//System.err.println("bwFactor "+bwFactor);

		Integer pambs = (Integer)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_pambs);
		//System.err.println("pambs "+pambs);
		if (pambs == 0)
			throw new WrongParametersException(ForestFireGeneratorAlgo.INPUT_pambs+" should be > 0");
		
		return IgraphLibFactory.getImplementation((Integer) algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_IMPLEMENTATION))
				.generateForestFire(
				N, 
				fwProb, 
				bwFactor, 
				pambs, 
				(Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_DIRECTED.getId()),
				(Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_SIMPLIFY_MULTI.getId()),
				(Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_SIMPLIFY_LOOPS.getId()),
				this.exec,
				(Long)algoInst.getValueForParameter(AbstractIGraphGenerator.PARAM_SEED)
				);
		
	}

	
}
