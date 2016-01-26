package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadPajekExec extends AbstractIGraphReaderExec {

	public ReadPajekExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);

	}

	public ReadPajekExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename) {
		
		return getLibrary().readGraphPajek(filename, exec);
	}

}
