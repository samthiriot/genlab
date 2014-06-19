package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.GenlabProgressCallback;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

public class ForestFireGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_N = new IntegerInOut(
			"in_N", 
			"N", 
			"Number of vertices to create"
			);
	
	public static final DoubleInOut INPUT_fw_prob = new DoubleInOut(
			"in_fw_prob", 
			"fw_prob", 
			"The forward burning probability."
			);
	
	public static final DoubleInOut INPUT_bw_factor = new DoubleInOut(
			"in_bw_factor", 
			"bw_factor", 
			"The backward burning ratio. The backward burning probability is calculated as bw.factor*fw.prob ."
			);
	

	public static final IntegerInOut INPUT_pambs = new IntegerInOut(
			"in_pambs", 
			"pambs", 
			"The number of ambassador vertices."
			);
	
	public static final BooleanParameter PARAM_DIRECTED = new BooleanParameter(
			"param_directed", 
			"directed", 
			"generate directed graphs", 
			false
			);
	
	
	public static final BooleanParameter PARAM_SIMPLIFY_MULTI = new BooleanParameter(
			"param_simplify_multi", 
			"simplify", 
			"remove the double links created during the generation; if false, the network will be a multigraph", 
			true
			);
	
	public static final BooleanParameter PARAM_SIMPLIFY_LOOPS = new BooleanParameter(
			"param_simplify_loops", 
			"remove loops", 
			"remove the loops created during the generation", 
			false
			);
	
	
	
	public ForestFireGeneratorAlgo() {
		super(
				"Forest Fire  (igraph)", 
				"The forest fire model intends to reproduce the following network characteristics, observed in real networks."
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_fw_prob);
		inputs.add(INPUT_bw_factor);
		inputs.add(INPUT_pambs);

		registerParameter(PARAM_DIRECTED);
		registerParameter(PARAM_SIMPLIFY_MULTI);
		registerParameter(PARAM_SIMPLIFY_LOOPS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractIGraphGeneratorExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 1000;
			}
			
			@Override
			protected IGraphGraph generateGraph(IGraphLibrary lib,
					ListOfMessages messages) {
				
				try {
					lib.installProgressCallback(new GenlabProgressCallback(progress));
					
					Integer N = (Integer)getInputValueForInput(INPUT_N);
					//System.err.println("N "+N);
					Double fwProb = (Double)getInputValueForInput(INPUT_fw_prob);
					if (fwProb == 0.0)
						throw new WrongParametersException(INPUT_fw_prob+" should be > 0");
						
					//System.err.println("fw "+fwProb);

					Double bwFactor = (Double)getInputValueForInput(INPUT_bw_factor);
					//System.err.println("bwFactor "+bwFactor);

					Integer pambs = (Integer)getInputValueForInput(INPUT_pambs);
					//System.err.println("pambs "+pambs);
					if (pambs == 0)
						throw new WrongParametersException(INPUT_pambs+" should be > 0");
					
					IGraphGraph g = lib.generateForestFire(
							N, 
							fwProb, 
							bwFactor, 
							pambs, 
							(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId())
							);
					
					boolean simplifyMultiple = (Boolean)algoInst.getValueForParameter(PARAM_SIMPLIFY_MULTI.getId());
					boolean simplifyLoops = (Boolean)algoInst.getValueForParameter(PARAM_SIMPLIFY_LOOPS.getId());
					if (simplifyMultiple || simplifyLoops) {
						lib.simplifyGraph(g, simplifyMultiple, simplifyLoops);
		
					}
					
					return g;
				} finally {
					lib.uninstallProgressCallback();
				}
			}
		};
	}

}
