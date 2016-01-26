package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadGraphDBExec extends
		AbstractIGraphReaderWidthDirectionalityExec {

	public ReadGraphDBExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ReadGraphDBExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename, boolean directed) {
		return getLibrary().readGraphGraphDB(filename, exec, directed);
	}

}
