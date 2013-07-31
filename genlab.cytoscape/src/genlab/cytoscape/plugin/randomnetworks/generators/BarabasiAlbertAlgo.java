package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import cytoscape.randomnetwork.BarabasiAlbertModel;
import cytoscape.randomnetwork.RandomNetworkModel;

public class BarabasiAlbertAlgo extends RandomNetworkGeneratorAlgo {

	public static IntegerInOut INPUT_N = new IntegerInOut("in_N", "N", "number of vertices to create");
	public static IntegerInOut INPUT_M = new IntegerInOut("in_m", "m", "number of edges to add at each iteration");
	public static IntegerInOut INPUT_PINIT = new IntegerInOut("in_pinit", "pinit", "number of nodes in the seed network");
	
	public BarabasiAlbertAlgo() {
		super(
				"Barabasi-Albert scale-free (cytoscape)", 
				"TODO"
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_M);
		inputs.add(INPUT_PINIT);

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
				
				BarabasiAlbertModel model = new BarabasiAlbertModel(
						(Integer)getInputValueForInput(INPUT_N), 
						(Boolean)algoInst.getValueForParameter(PARAM_LOOPS.getId()), 
						(Boolean)algoInst.getValueForParameter(PARAM_DIRECTED.getId()), 
						(Integer)getInputValueForInput(INPUT_PINIT), 
						(Integer)getInputValueForInput(INPUT_M)
						);
				
				
				return model;
			}
		};
	}

}


