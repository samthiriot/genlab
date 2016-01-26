package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadNcolExec extends
		AbstractIGraphReaderWidthDirectionalityExec {

	public ReadNcolExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ReadNcolExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename, boolean directed) {
		return getLibrary().readGraphNcol(filename, exec, directed);
	}

}
