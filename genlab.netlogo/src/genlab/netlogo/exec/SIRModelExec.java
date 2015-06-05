package genlab.netlogo.exec;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.netlogo.NetlogoUtils;
import genlab.netlogo.RunNetlogoModel;
import genlab.netlogo.algos.SIRModelAlgo;

public class SIRModelExec extends AbstractAlgoExecutionOneshot {

	private boolean cancel = false;
	
	public SIRModelExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	public SIRModelExec() {
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
			final IGenlabGraph graph = (IGenlabGraph)getInputValueForInput(SIRModelAlgo.INPUT_GRAPH);
			final Integer outbreak = (Integer)getInputValueForInput(SIRModelAlgo.INPUT_OUTBREAK);
			
			final Double spread = (Double)getInputValueForInput(SIRModelAlgo.INPUT_SPREAD_CHANCE);
			final Double recover = (Double)getInputValueForInput(SIRModelAlgo.INPUT_RECOVER_CHANCE);
			final Double resistance = (Double)getInputValueForInput(SIRModelAlgo.INPUT_RESISTANCE_CHANCE);
			
			final Integer maxStep = (Integer) algoInst.getValueForParameter(SIRModelAlgo.PARAM_MAX_STEPS);
			final Boolean openGui = (Boolean)algoInst.getValueForParameter(SIRModelAlgo.PARAM_GUI);
			
			progress.setProgressMade(1);
	
			// write the graph somewhere so it can be read by netlogo
			File fileNetwork = NetlogoUtils.writeGraphToNetlogoGraphML(graph, messages);
			progress.setProgressMade(2);
	
			// define inputs
			Map<String,Object> inputs = new HashMap<String, Object>();
			inputs.put("network-filename", fileNetwork.getAbsolutePath());
			inputs.put("initial-outbreak-size", outbreak);
			inputs.put("virus-spread-chance", (int)Math.round(spread*100));
			inputs.put("virus-check-frequency", 1);
			inputs.put("recovery-chance", (int)Math.round(recover*100));
			inputs.put("gain-resistance-chance", (int)Math.round(resistance*100));
			inputs.put("is-graphical", openGui.booleanValue());

			// define outputs
			Collection<String> requiredOutputs = new LinkedList<String>();
			requiredOutputs.add("measure-susceptible");
			requiredOutputs.add("measure-infected");
			requiredOutputs.add("measure-resistant");
					
			progress.setProgressMade(3);
	
			String fileAbsolute = NetlogoUtils.findAbsolutePathForRelativePath("genlab.netlogo/ressources/models/Virus on a Network.nlogo");
			
			if (fileAbsolute == null) {
				throw new ProgramException("Unable to find file for the Netlogo model");
			}
				
			// run the model
			Map<String,Object> result = null;
			if (openGui) {
				GLLogger.warnUser("once open, Netlogo cannot be closed. It will close itself when you close Netlogo. Sorry.", getClass());
				result = RunNetlogoModel.runNetlogoModelGraphical(
						messages, 
						fileAbsolute, 
						inputs, 
						requiredOutputs,
						maxStep,
						progress
						);
			} else {
				result = RunNetlogoModel.runNetlogoModelHeadless(
					messages, 
					"genlab.netlogo/ressources/models/Virus on a Network.nlogo", 
					inputs, 
					requiredOutputs,
					maxStep,
					progress
					);
				fileNetwork.delete();
			}
			// transmit results
			res.setResult(SIRModelAlgo.OUTPUT_INFECTED, result.get("measure-infected"));
			res.setResult(SIRModelAlgo.OUTPUT_SUSCEPTIBLE, result.get("measure-susceptible"));
			res.setResult(SIRModelAlgo.OUTPUT_RESISTANT, result.get("measure-resistant"));
			res.setResult(SIRModelAlgo.OUTPUT_DURATION, result.get("_duration"));

			progress.setProgressMade(10);
			
			progress.setComputationState(ComputationState.FINISHED_OK);

		} catch (RuntimeException e) {
			messages.errorUser("failed: "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		}

	}

	@Override
	public void cancel() {
		cancel = true;
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void kill() {
		cancel = true;
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

}
