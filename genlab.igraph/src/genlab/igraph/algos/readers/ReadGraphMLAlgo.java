package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadGraphMLAlgo extends AbstractIGraphReaderAlgo {

	public ReadGraphMLAlgo() {
		super(
				"read graph from GraphML (igraph)", 
				"read a graph from a file structured in GraphML, as implemented in igraph"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadGraphMLExec(execution, algoInstance);
	}


}
