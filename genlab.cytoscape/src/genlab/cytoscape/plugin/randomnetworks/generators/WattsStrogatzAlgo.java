package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import cytoscape.randomnetwork.RandomNetworkModel;
import cytoscape.randomnetwork.WattsStrogatzModel;

public class WattsStrogatzAlgo extends RandomNetworkGeneratorAlgo {

	public static IntegerInOut INPUT_N = new IntegerInOut("in_N", "N", "number of vertices to create", 200);
	public static IntegerInOut INPUT_DEGREE = new IntegerInOut("in_degree", "degree", "degree of each vertex", 2);
	public static DoubleInOut INPUT_P = new DoubleInOut("in_p", "p", "rewiring probability", 0.05);
	

	public WattsStrogatzAlgo() {
		super(
				"watts strogatz beta (cytoscape)", 
				"TODO"
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_DEGREE);
		inputs.add(INPUT_P);

	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new RandomNetworkGeneratorAlgoExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 1000;
			}
			
			@Override
			protected RandomNetworkModel getModel() {
				
				
				
				RandomNetworkModel model = new WattsStrogatzModel(
						(Integer)getInputValueForInput(INPUT_N), 
						(Boolean)algoInst.getValueForParameter(PARAM_LOOPS.getId()), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId()), 
						(Double)getInputValueForInput(INPUT_P),
						(Integer)getInputValueForInput(INPUT_DEGREE)
						);
				
				
				return model;
			}
		};
	}

}


