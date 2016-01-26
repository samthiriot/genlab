package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WriteDotExec extends AbstractIGraphWriterExec {

	public WriteDotExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public WriteDotExec() {
	}

	@Override
	protected void writeGraph(IGenlabGraph graph, String filename) {

		getLibrary().writeGraphDot(graph, filename, this.exec);
	}




}
