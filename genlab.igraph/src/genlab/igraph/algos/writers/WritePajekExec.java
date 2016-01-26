package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WritePajekExec extends AbstractIGraphWriterExec {

	public WritePajekExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public WritePajekExec() {
	}

	@Override
	protected void writeGraph(IGenlabGraph graph, String filename) {

		getLibrary().writeGraphPajek(graph, filename, this.exec);
	}




}
