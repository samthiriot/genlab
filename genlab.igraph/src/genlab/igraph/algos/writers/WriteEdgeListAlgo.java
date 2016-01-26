package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class WriteEdgeListAlgo extends AbstractIGraphWriterAlgo {

	public WriteEdgeListAlgo() {
		super(
				"write as edgelist", 
				"write a graph in edgelist format", 
				EIgraphImplementation.R_ONLY,
				".edgelist"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new WriteEdgeListExec(execution, algoInstance);
	}

}
