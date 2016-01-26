package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadPajekAlgo extends AbstractIGraphReaderAlgo {

	public ReadPajekAlgo() {
		super(
				"read graph from Pajek (igraph)", 
				"read a graph from a file structured in Pajek, as implemented in igraph"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadPajekExec(execution, algoInstance);
	}


}
