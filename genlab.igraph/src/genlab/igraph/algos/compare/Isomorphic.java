package genlab.igraph.algos.compare;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.igraph.natjna.IGraphGraph;

public class Isomorphic extends BasicIsomorphism {

	public Isomorphic() {
		super(
				"isomorphism (igraph)", 
				"returns true is the two graphs are isomorphic. The isomorphism algorithm to be used depends on the input graphs: BLISS for undirected graphs, VF2 for directed graphs"
				);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractIsomorphismExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 1000;
			}
			
			@Override
			protected boolean compareGraphs(IGraphGraph igraphGraph1,
					IGraphGraph igraphGraph2) {
				return igraphGraph1.lib.computeIsomorphicm(igraphGraph1, igraphGraph2);
				
			}
		};
	}

}
