package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadGraphDBAlgo extends
		AbstractIGraphReaderWithDirectionalityAlgo {

	public ReadGraphDBAlgo() {
		super("read graph from GraphDB (igraph)", "read a graph from a file as GraphDB");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadGraphDBExec(execution, algoInstance);
	}

}
