package genlab.netlogo.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.netlogo.NetlogoUtils;
import genlab.netlogo.RunNetlogoModel;
import genlab.netlogo.algos.NetlogoModelAlgo;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NetlogoModelExec extends AbstractAlgoExecutionOneshot {

	private boolean cancel = false;
	
	public NetlogoModelExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	public NetlogoModelExec() {
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected Object getNetlogoValueForValue(Object v) {
		if (v instanceof Double) {
			Double d = (Double)v;
			if (Math.round(d) == d.doubleValue()) {
				return new Integer(d.intValue());
			} else {
				return d;
			}
		} else {
			return v;
		}
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
			final Integer maxStep = (Integer) algoInst.getValueForParameter(NetlogoModelAlgo.PARAM_MAX_STEPS);
			final Boolean openGui = (Boolean)algoInst.getValueForParameter(NetlogoModelAlgo.PARAM_GUI);
			final File modelFile = (File)algoInst.getValueForParameter(NetlogoModelAlgo.PARAM_NETLOGO_MODEL);
			
			progress.setProgressMade(1);
		
			// define inputs
			Map<String,Object> inputs = new HashMap<String, Object>();
			for (IInputOutputInstance inputInst: algoInst.getInputInstances()) {
				inputs.put(
						inputInst.getMeta().getName(), 
						getNetlogoValueForValue(getInputValueForInput(inputInst))
						);

			}

			// define outputs
			Collection<String> requiredOutputs = new LinkedList<String>();
			for (IInputOutputInstance outputInst: algoInst.getOutputInstances()) {
				// ignore the outputs we already process elsewhere
				if (outputInst.getMeta().equals(NetlogoModelAlgo.OUTPUT_DURATION))
					continue;
				// and add these ones
				requiredOutputs.add(outputInst.getMeta().getName());
			}
			progress.setProgressMade(3);
	
			String fileAbsolute = NetlogoUtils.findAbsolutePathForRelativePath(modelFile.getAbsolutePath());
			
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
					fileAbsolute, 
					inputs, 
					requiredOutputs,
					maxStep,
					progress
					);
			}
			// transmit results
			// define outputs
			res.setResult(NetlogoModelAlgo.OUTPUT_DURATION, result.get("_duration"));
			for (IInputOutputInstance outputInst: algoInst.getOutputInstances()) {
				// ignore the outputs we already process elsewhere
				if (outputInst.getMeta().equals(NetlogoModelAlgo.OUTPUT_DURATION))
					continue;
				// and add these ones
				res.setResult(
						outputInst.getMeta(), 
						result.get(outputInst.getMeta().getName())
						);

			}
			progress.setProgressMade(140);
			
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
