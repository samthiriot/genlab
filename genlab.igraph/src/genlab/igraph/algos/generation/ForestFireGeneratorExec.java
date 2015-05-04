package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class ForestFireGeneratorExec extends AbstractIGraphGeneratorExec {
		
	public ForestFireGeneratorExec() {}

	public ForestFireGeneratorExec(IExecution execution,
			AlgoInstance algoInstance) {
		super(execution, algoInstance);
	}

	@Override
	public long getTimeout() {
		return 1000;
	}
	
	@Override
	protected IGraphGraph generateGraph(IGraphLibrary lib,
			ListOfMessages messages) {
		
		//GenlabProgressCallback callback = new GenlabProgressCallback(progress);
		//GenlabProgressCallback.keepStrongReference(callback);
		try {
			//lib.installProgressCallback(callback);
			
			Integer N = (Integer)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_N);
			//System.err.println("N "+N);
			Double fwProb = (Double)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_fw_prob);
			if (fwProb == 0.0)
				throw new WrongParametersException(ForestFireGeneratorAlgo.INPUT_fw_prob+" should be > 0");
				
			//System.err.println("fw "+fwProb);

			Double bwFactor = (Double)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_bw_factor);
			//System.err.println("bwFactor "+bwFactor);

			Integer pambs = (Integer)getInputValueForInput(ForestFireGeneratorAlgo.INPUT_pambs);
			//System.err.println("pambs "+pambs);
			if (pambs == 0)
				throw new WrongParametersException(ForestFireGeneratorAlgo.INPUT_pambs+" should be > 0");
			
			IGraphGraph g = lib.generateForestFire(
					N, 
					fwProb, 
					bwFactor, 
					pambs, 
					(Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_DIRECTED.getId())
					);
			
			boolean simplifyMultiple = (Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_SIMPLIFY_MULTI.getId());
			boolean simplifyLoops = (Boolean)algoInst.getValueForParameter(ForestFireGeneratorAlgo.PARAM_SIMPLIFY_LOOPS.getId());
			if (simplifyMultiple || simplifyLoops) {
				lib.simplifyGraph(g, simplifyMultiple, simplifyLoops);

			}
			
			return g;
		} finally {
			//lib.uninstallProgressCallback();
			//GenlabProgressCallback.removeStrongReference(callback);
		}
	}
}
