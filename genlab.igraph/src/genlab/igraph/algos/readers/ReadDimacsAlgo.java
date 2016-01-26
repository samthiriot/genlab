package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadDimacsAlgo extends
		AbstractIGraphReaderWithDirectionalityAlgo {

	public ReadDimacsAlgo() {
		super("read graph from Dimacs (igraph)", "read a graph from a file as Dimacs");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadDimacsExec(execution, algoInstance);
	}

}
