package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class WritePajekAlgo extends AbstractIGraphWriterAlgo {

	public WritePajekAlgo() {
		super(
				"write as Pajek", 
				"write a graph in Pajek format", 
				EIgraphImplementation.R_ONLY,
				".pajek"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new WritePajekExec(execution, algoInstance);
	}

}
