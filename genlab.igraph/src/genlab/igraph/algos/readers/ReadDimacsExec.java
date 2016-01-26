package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadDimacsExec extends
		AbstractIGraphReaderWidthDirectionalityExec {

	public ReadDimacsExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public ReadDimacsExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename, boolean directed) {
		return getLibrary().readGraphDIMACS(filename, exec, directed);
	}

}
