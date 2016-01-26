package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class AbstractIGraphReaderWidthDirectionalityExec extends
		AbstractIGraphReaderExec {

	public AbstractIGraphReaderWidthDirectionalityExec(IExecution exec,
			IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public AbstractIGraphReaderWidthDirectionalityExec() {
	}

	protected abstract IGenlabGraph readGraph(String filename, boolean directed);
	
	@Override
	protected final IGenlabGraph readGraph(String filename) {
		return readGraph(
				filename, 
				(Boolean)algoInst.getValueForParameter(AbstractIGraphReaderWithDirectionalityAlgo.PARAM_DIRECTED)
				);
	}

}
