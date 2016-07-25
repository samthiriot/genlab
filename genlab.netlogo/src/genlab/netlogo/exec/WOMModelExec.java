package genlab.netlogo.exec;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.netlogo.NetlogoUtils;
import genlab.netlogo.RunNetlogoModel;
import genlab.netlogo.algos.SIRVaccinesModelAlgo;
import genlab.netlogo.algos.WOMModelAlgo;;

public class WOMModelExec extends AbstractAlgoExecutionOneshot {

	public WOMModelExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}
	

	public WOMModelExec() {
	}
	
	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {

		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
		
		progress.setProgressTotal(10+130);
		progress.setProgressMade(0);
		progress.setComputationState(ComputationState.STARTED);
		
		try {
			// retrieve inputs
			final IGenlabGraph graph = (IGenlabGraph)getInputValueForInput(WOMModelAlgo.INPUT_GRAPH);
			final Integer maxStep = (Integer) algoInst.getValueForParameter(SIRVaccinesModelAlgo.PARAM_MAX_STEPS);

			progress.setProgressMade(1);
			
			// write the graph somewhere so it can be read by netlogo
			File fileNetwork = NetlogoUtils.writeGraphToNetlogoGML(graph);
			progress.setProgressMade(2);
	
			// define inputs
			Map<String,Object> inputs = new HashMap<String, Object>();
			inputs.put("network-filename", fileNetwork.getAbsolutePath());
			inputs.put("initial-proportion-knowledgeable", (Double)getInputValueForInput(WOMModelAlgo.INPUT_PROPORTION_KNOWLEDGEABLE));
			inputs.put("proportion-active", (Double)getInputValueForInput(WOMModelAlgo.INPUT_PROPORTION_SEEKERS));
			inputs.put("proportion-promoters", (Double)getInputValueForInput(WOMModelAlgo.INPUT_PROPORTION_PROMOTERS));
			inputs.put("duration-seek", (Integer)getInputValueForInput(WOMModelAlgo.INPUT_DURATION_SEEKERS));
			inputs.put("duration-proactive", (Integer)getInputValueForInput(WOMModelAlgo.INPUT_DURATION_PROMOTERS));
			inputs.put("with-gui", false);
			inputs.put("advertisement-duration", (Integer)getInputValueForInput(WOMModelAlgo.INPUT_DURATION_AD));
			inputs.put("advertisement-proportion-per-step", (Double)getInputValueForInput(WOMModelAlgo.INPUT_IMPACT_AD));

			// define outputs
			Collection<String> requiredOutputs = new LinkedList<String>();
			requiredOutputs.add("result-A");
			requiredOutputs.add("result-AK");
			requiredOutputs.add("ticks-for-peak-A");
			requiredOutputs.add("ticks-for-peak-AK");
			requiredOutputs.add("ticks-last-activity");
					
			progress.setProgressMade(3);
	
			final File fileModel = NetlogoUtils.findFileInPlugin("ressources/models/third_external_network.nlogo");
				
			// run the model
			Map<String,Object> result = null;
			result = RunNetlogoModel.runNetlogoModelHeadless(
				messages, 
				fileModel.getAbsolutePath(), 
				inputs, 
				requiredOutputs,
				maxStep,
				progress
				);
			fileNetwork.delete();
			
			res.setResult(WOMModelAlgo.OUTPUT_A, result.get("result-A"));
			res.setResult(WOMModelAlgo.OUTPUT_AK, result.get("result-AK"));
			res.setResult(WOMModelAlgo.OUTPUT_DURATION, result.get("_duration"));
			res.setResult(WOMModelAlgo.OUTPUT_PEAK_A, result.get("ticks-for-peak-A"));
			res.setResult(WOMModelAlgo.OUTPUT_PEAK_AK, result.get("ticks-for-peak-AK"));
			res.setResult(WOMModelAlgo.OUTPUT_LAST_ACTIVITY, result.get("ticks-last-activity"));
			
			progress.setProgressMade(100);
			
			progress.setComputationState(ComputationState.FINISHED_OK);

		} catch (RuntimeException e) {
			messages.errorUser("failed: "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		}
	}

	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

}
