package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WriteEdgeListExec extends AbstractIGraphWriterExec {

	public WriteEdgeListExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public WriteEdgeListExec() {
	}

	@Override
	protected void writeGraph(IGenlabGraph graph, String filename) {

		getLibrary().writeGraphEdgelist(graph, filename, this.exec);
	}




}
