package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class WriteGraphMLAlgo extends AbstractIGraphWriterAlgo {

	public WriteGraphMLAlgo() {
		super(
				"write as GraphML", 
				"write a graph in GraphML format", 
				EIgraphImplementation.R_ONLY,
				".graphml"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new WriteGraphMLExec(execution, algoInstance);
	}

}
