package genlab.igraph.algos.generation;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.core.usermachineinteraction.ListOfMessages;
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

				return lib.generateForestFire(
						(Integer)getInputValueForInput(INPUT_N), 
						(Double)getInputValueForInput(INPUT_fw_prob), 
						(Double)getInputValueForInput(INPUT_bw_factor), 
						(Integer)getInputValueForInput(INPUT_pambs), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId())
						);
				
			}
		};
	}

}
