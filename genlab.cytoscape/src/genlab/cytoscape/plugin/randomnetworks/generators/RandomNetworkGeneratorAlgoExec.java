package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.cytoscape.plugin.randomnetworks.Converter;
import cytoscape.randomnetwork.RandomNetwork;
import cytoscape.randomnetwork.RandomNetworkModel;

public abstract class RandomNetworkGeneratorAlgoExec extends AbstractAlgoExecution {

	public RandomNetworkGeneratorAlgoExec(IExecution exec,
			IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	protected abstract RandomNetworkModel getModel();
	

	@Override
	public void run() {
		
		progress.setComputationState(ComputationState.STARTED);
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		RandomNetworkModel model = getModel();
		model.setCreateView(false);
		
		RandomNetwork network = model.generate();
		
		// TODO directionality
		
		result.setResult(
				RandomNetworkGeneratorAlgo.OUTPUT_GRAPH, 
				Converter.getGenlabGraphForCytoscape(
						network, 
						(Boolean)algoInst.getValueForParameter(RandomNetworkGeneratorAlgo.PARAM_DIRECTED.getId())
						)
				);
		
		
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
