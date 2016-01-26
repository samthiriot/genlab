package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadEdgelistExec extends
		AbstractIGraphReaderWidthDirectionalityExec {

	public ReadEdgelistExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ReadEdgelistExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename, boolean directed) {
		return getLibrary().readGraphEdgelist(filename, exec, directed);
	}

}
