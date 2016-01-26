package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WriteGraphMLExec extends AbstractIGraphWriterExec {

	public WriteGraphMLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public WriteGraphMLExec() {
	}

	@Override
	protected void writeGraph(IGenlabGraph graph, String filename) {

		getLibrary().writeGraphGraphML(graph, filename, this.exec);
	}




}
