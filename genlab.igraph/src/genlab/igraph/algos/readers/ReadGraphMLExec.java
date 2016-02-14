package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadGraphMLExec extends AbstractIGraphReaderExec {

	public ReadGraphMLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);

	}

	public ReadGraphMLExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename) {
		
		return getLibrary().readGraphGraphML(filename, exec);
	}

}
