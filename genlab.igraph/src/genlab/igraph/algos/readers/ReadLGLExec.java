package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ReadLGLExec extends AbstractIGraphReaderExec {

	public ReadLGLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);

	}

	public ReadLGLExec() {
	}

	@Override
	protected IGenlabGraph readGraph(String filename) {
		
		return getLibrary().readGraphLGL(filename, exec);
	}

}
