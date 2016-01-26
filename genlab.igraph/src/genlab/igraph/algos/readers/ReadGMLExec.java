package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadGMLExec extends AbstractIGraphReaderExec {

	public ReadGMLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);

	}

	public ReadGMLExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename) {
		
		return getLibrary().readGraphGML(filename, exec);
	}

}
