package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadDLAlgo extends
		AbstractIGraphReaderWithDirectionalityAlgo {

	public ReadDLAlgo() {
		super("read graph from DL (igraph)", "read a graph from a file as DL");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadDLExec(execution, algoInstance);
	}

}
