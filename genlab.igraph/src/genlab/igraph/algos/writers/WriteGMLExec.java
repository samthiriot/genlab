package genlab.igraph.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WriteGMLExec extends AbstractIGraphWriterExec {

	public WriteGMLExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	public WriteGMLExec() {
	}

	@Override
	protected void writeGraph(IGenlabGraph graph, String filename) {

		getLibrary().writeGraphGML(graph, filename, this.exec);
	}




}
