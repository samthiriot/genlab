package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public class WriteGMLAlgo extends AbstractIGraphWriterAlgo {

	public WriteGMLAlgo() {
		super(
				"write as GML", 
				"write a graph in GML format", 
				EIgraphImplementation.R_ONLY,
				".gml"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new WriteGMLExec(execution, algoInstance);
	}

}
