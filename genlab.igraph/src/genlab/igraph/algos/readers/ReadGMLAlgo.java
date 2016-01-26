package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadGMLAlgo extends AbstractIGraphReaderAlgo {

	public ReadGMLAlgo() {
		super(
				"read graph from GML (igraph)", 
				"read a graph from a file structured in GML, as implemented in igraph"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadGMLExec(execution, algoInstance);
	}


}
