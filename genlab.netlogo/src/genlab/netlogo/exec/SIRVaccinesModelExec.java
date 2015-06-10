package genlab.netlogo.exec;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
import genlab.netlogo.algos.SIRVaccinesModelAlgo;

public class SIRVaccinesModelExec extends AbstractAlgoExecutionOneshot {

	private boolean cancel = false;
	
	public SIRVaccinesModelExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	public SIRVaccinesModelExec() {
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected SortedSet<String> findHigestValues(final IGenlabGraph graph, int count, final String attributeId) {
				
		TreeSet<String> bests = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				Number n1 = (Number) graph.getVertexAttributeValue(arg0, attributeId);
				Number n2 = (Number) graph.getVertexAttributeValue(arg1, attributeId);
				return Double.compare(n1.doubleValue(), n2.doubleValue());
			}
	
		});
		
		double lowerValue = Double.MAX_VALUE;
		for (String vertexId: graph.getVertices()) {
			
			Double value = ((Number)graph.getVertexAttributeValue(vertexId, attributeId)).doubleValue();
			
			if (bests.size() < count) {
				bests.add(vertexId);
				lowerValue = Math.min(value, lowerValue);
			} else {
				
				// if we are higher than the lowest selected, then keep it
				if (value > lowerValue) {
					// keep that as the lowest value 
					lowerValue = value;
					// remove the first one (which happens to be the lower one)
					Iterator<String> it = bests.iterator();
					it.next();
					it.remove();
					bests.add(vertexId);
				}
				
			}
			
		}
		
		return bests;
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
			final IGenlabGraph graph = (IGenlabGraph)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_GRAPH);
			final Integer outbreak = (Integer)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_OUTBREAK);
			
			final Double spread = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_SPREAD_CHANCE);
			final Double recover = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_RECOVER_CHANCE);
			final Double resistance = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_RESISTANCE_CHANCE);
			
			final Integer maxStep = (Integer) algoInst.getValueForParameter(SIRVaccinesModelAlgo.PARAM_MAX_STEPS);
			final Boolean openGui = (Boolean)algoInst.getValueForParameter(SIRVaccinesModelAlgo.PARAM_GUI);
			
			final Integer countVaccinesDegree = (Integer)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_HIGHEST_DEGREE);
			final Integer countVaccinesBetweeness = (Integer)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_HIGHEST_BETWEENESS);
			
			progress.setProgressMade(1);
		
			final String attributeValue = "igraph_node_betweeness";
			
			// declare fields
			graph.declareVertexAttribute("presistant", Boolean.class);
			// TODO create vaccination !
			if (countVaccinesBetweeness > 0) {
				SortedSet<String> highestBetweeness = findHigestValues(
														graph, 
														countVaccinesBetweeness, 
														attributeValue
														);
	
				messages.infoUser("will vaccinate nodes with highest betweeness: "+highestBetweeness, getClass());
				for (String vertexId: graph.getVertices()) {
					graph.setVertexAttribute(vertexId, "presistant", highestBetweeness.contains(vertexId));
				}
				
			}
			
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
	
			String fileAbsolute = NetlogoUtils.findAbsolutePathForRelativePath("genlab.netlogo/ressources/models/Virus on a Network vaccines.nlogo");
			
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
