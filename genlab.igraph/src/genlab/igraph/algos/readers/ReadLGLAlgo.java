package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class ReadLGLAlgo extends AbstractIGraphReaderAlgo {

	public ReadLGLAlgo() {
		super(
				"read graph from LGL (igraph)", 
				"read a graph from a file structured in LGL format, as implemented in igraph"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ReadLGLExec(execution, algoInstance);
	}


}
