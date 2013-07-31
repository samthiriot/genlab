package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import cytoscape.randomnetwork.ErdosRenyiModel;
import cytoscape.randomnetwork.RandomNetworkModel;

public class ERGNMAlgo extends RandomNetworkGeneratorAlgo {

	public static IntegerInOut INPUT_N = new IntegerInOut("in_N", "N", "number of vertices to create");
	public static IntegerInOut INPUT_M = new IntegerInOut("in_m", "m", "number of edges to create");
	

	public ERGNMAlgo() {
		super(
				"Erdos-Renyi G(n,m) (cytoscape)", 
				"generates a simple random graph from the number of vertices and the probability to connect"
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_M);

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
						(Integer)getInputValueForInput(INPUT_M), 
						(Boolean)algoInst.getValueForParameter(PARAM_LOOPS.getId()), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId())
						);
						
						
				return model;
			}
		};
	}

}


