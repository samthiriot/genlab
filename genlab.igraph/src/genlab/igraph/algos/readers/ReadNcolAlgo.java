package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadNcolAlgo extends
		AbstractIGraphReaderWithDirectionalityAlgo {

	public ReadNcolAlgo() {
		super("read graph from Ncol (igraph)", "read a graph from a file as Ncol");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadNcolExec(execution, algoInstance);
	}

}
