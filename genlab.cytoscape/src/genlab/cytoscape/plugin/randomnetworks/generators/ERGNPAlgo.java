package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import cytoscape.randomnetwork.ErdosRenyiModel;
import cytoscape.randomnetwork.RandomNetworkModel;

public class ERGNPAlgo extends RandomNetworkGeneratorAlgo {

	public static IntegerInOut INPUT_N = new IntegerInOut("in_N", "N", "number of vertices to create");
	public static DoubleInOut INPUT_P = new DoubleInOut("in_p", "p", "probability to create edges");
	

	public ERGNPAlgo() {
		super(
				"Erdos-Renyi G(n,p) (cytoscape)", 
				"generates a simple random graph from the number of vertices and the probability to connect"
				);
		
		inputs.add(INPUT_N);
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
				
				
				
				RandomNetworkModel model = new ErdosRenyiModel(
						(Integer)getInputValueForInput(INPUT_N),
						(Double)getInputValueForInput(INPUT_P), 
						(Boolean)algoInst.getValueForParameter(PARAM_LOOPS.getId()), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId())
						);
						
						
				return model;
			}
		};
	}

}


