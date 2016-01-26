package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class WriteDotAlgo extends AbstractIGraphWriterAlgo {

	public WriteDotAlgo() {
		super(
				"write as dot", 
				"write a graph in dot format", 
				EIgraphImplementation.R_ONLY,
				".dot"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new WriteDotExec(execution, algoInstance);
	}

}
