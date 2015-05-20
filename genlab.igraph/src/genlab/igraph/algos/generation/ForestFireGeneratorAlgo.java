package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;

public class ForestFireGeneratorAlgo extends AbstractIGraphGenerator {

	public static final IntegerInOut INPUT_N = new IntegerInOut(
			"in_N", 
			"N", 
			"Number of vertices to create",
			200
			);
	
	public static final DoubleInOut INPUT_fw_prob = new DoubleInOut(
			"in_fw_prob", 
			"fw_prob", 
			"The forward burning probability.",
			0.3
			);
	
	public static final DoubleInOut INPUT_bw_factor = new DoubleInOut(
			"in_bw_factor", 
			"bw_factor", 
			"The backward burning ratio. The backward burning probability is calculated as bw.factor*fw.prob .",
			0.08
			);
	

	public static final IntegerInOut INPUT_pambs = new IntegerInOut(
			"in_pambs", 
			"pambs", 
			"The number of ambassador vertices.",
			2
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
				"The forest fire model intends to reproduce the following network characteristics, observed in real networks.",
				true
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
		
		return new ForestFireGeneratorExec(execution, algoInstance);
	}

}
