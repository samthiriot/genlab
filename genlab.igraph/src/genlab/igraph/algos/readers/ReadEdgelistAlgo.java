package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadEdgelistAlgo extends
		AbstractIGraphReaderWithDirectionalityAlgo {

	public ReadEdgelistAlgo() {
		super("read graph from edge list (igraph)", "read a graph from a file as edge list");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadEdgelistExec(execution, algoInstance);
	}

}
