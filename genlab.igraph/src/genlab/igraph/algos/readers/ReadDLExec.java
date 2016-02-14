package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadDLExec extends
		AbstractIGraphReaderWidthDirectionalityExec {

	public ReadDLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ReadDLExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename, boolean directed) {
		return getLibrary().readGraphDL(filename, exec, directed);
	}

}
