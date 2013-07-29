package genlab.igraph.algos.compare;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.BooleanInOut;
import genlab.igraph.natjna.IGraphGraph;

public class VF2Isomorphic extends BasicIsomorphism {


	public static final BooleanInOut OUTPUT_ISOMORPHIC_COUNT = new BooleanInOut(
			"out_isocount", 
			"count", 
			"count of isomorphisms"
			);
	
	public VF2Isomorphic() {
		super(
				"VF2 Isomorphism (igraph)", 
				"computes isomorphisms for two graphs having the same directivity"
				);
		
		outputs.add(OUTPUT_ISOMORPHIC_COUNT);
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
			public void noIsomorphismPossible(ComputationResult result) {
				super.noIsomorphismPossible(result);
				
				((ComputationResult)getResult()).setResult(OUTPUT_ISOMORPHIC_COUNT, 0);
			}
			
			@Override
			protected boolean compareGraphs(IGraphGraph igraphGraph1,
					IGraphGraph igraphGraph2) {
				
				Integer count = null;
				
				if (isUsed(OUTPUT_ISOMORPHIC_COUNT) || exec.getExecutionForced()) {

					count = igraphGraph1.lib.computeIsomorphismVF2Count(
							igraphGraph1, igraphGraph2
							);
					
					((ComputationResult)getResult()).setResult(OUTPUT_ISOMORPHIC_COUNT, count);
				}
				
				if ( (count != null) && !exec.getExecutionForced())
					// we will not compute again the same process; 
					return count > 0;
				else 
					return igraphGraph1.lib.computeIsomorphismVF2(
							igraphGraph1, igraphGraph2
						);
				
			}
		};
	}

}
