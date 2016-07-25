package genlab.netlogo.exec;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import cern.jet.random.Uniform;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.ListParameter;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.netlogo.NetlogoUtils;
import genlab.netlogo.RunNetlogoModel;
import genlab.netlogo.algos.SIRModelAlgo;
import genlab.netlogo.algos.SIRVaccinesModelAlgo;
import genlab.netlogo.inst.SIRVaccinesModelInstance;
import genlab.random.colt.ColtRandomGenerator;

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
				int doubleRes = Double.compare(n2.doubleValue(), n1.doubleValue());
				if (doubleRes == 0)
					return arg0.compareTo(arg1);
				else 
					return doubleRes;
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
					Iterator<String> it = bests.descendingIterator();
					it.next();
					it.remove();
					bests.add(vertexId);
				}
				
			}
			
		}
		/*
		StringBuffer sb = new StringBuffer("values for best: ");
		
		
		for (String v: bests) {
			sb.append(v);
			sb.append("=>");
			sb.append(graph.getVertexAttributeValue(v, attributeId));
			sb.append("; ");
		}
		System.out.println(sb.toString());
		*/
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
			

			final Integer countVaccines= (Integer)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_COUNT);
			final Double proportionVaccinesDegree = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_DEGREE);
			final Double proportionVaccinesBetweeness = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_BETWEENESS);
			final Double proportionVaccinesRandom = (Double)getInputValueForInput(SIRVaccinesModelAlgo.INPUT_VACCINE_RANDOM);
			final double norm = proportionVaccinesDegree + proportionVaccinesBetweeness + proportionVaccinesRandom;
			
			final Integer countVaccinesDegree = (int)Math.round((double)countVaccines*proportionVaccinesDegree/norm);
			final Integer countVaccinesBetweeness = (int)Math.round((double)countVaccines*proportionVaccinesBetweeness/norm);
			final Integer countVaccinesRandom = (int)Math.round((double)countVaccines*proportionVaccinesRandom/norm);
			
			progress.setProgressMade(1);
		
			String paramAttributeDegreeId;
			{
				final ListParameter paramForDegree = ((SIRVaccinesModelInstance)algoInst).PARAM_COLUMN_DEGREE;
				final Integer paramAttributeDegreeForVertexIdx = (Integer)algoInst.getValueForParameter(paramForDegree);
				paramAttributeDegreeId = paramForDegree.getLabel(paramAttributeDegreeForVertexIdx);
			}
			String paramAttributeBetweenessId;
			{
				final ListParameter paramForBetweeness = ((SIRVaccinesModelInstance)algoInst).PARAM_COLUMN_BETWEENESS;
				final Integer paramAttributeBetweenessForVertexIdx = (Integer)algoInst.getValueForParameter(paramForBetweeness);
				paramAttributeBetweenessId = paramForBetweeness.getLabel(paramAttributeBetweenessForVertexIdx);
			}
			
			// declare fields
			graph.declareVertexAttribute("presistant", Boolean.class);
			// TODO create vaccination !
			for (String vertexId: graph.getVertices()) {
				graph.setVertexAttribute(vertexId, "presistant", false);
			}
			if (countVaccinesBetweeness > 0) {
				SortedSet<String> highestBetweeness = findHigestValues(
														graph, 
														countVaccinesBetweeness, 
														paramAttributeBetweenessId
														);
	
				messages.infoUser("will vaccinate "+highestBetweeness.size()+" nodes with highest betweeness: "+highestBetweeness, getClass());
				for (String vertexId: highestBetweeness) {
					graph.setVertexAttribute(vertexId, "presistant", true);
				}
				
			} 
			if (countVaccinesDegree > 0) {
				SortedSet<String> highestDegree = findHigestValues(
														graph, 
														countVaccinesDegree, 
														paramAttributeDegreeId
														);
	
				messages.infoUser("will vaccinate "+highestDegree.size()+" nodes with highest degree: "+highestDegree, getClass());
				for (String vertexId: highestDegree) {
					graph.setVertexAttribute(vertexId, "presistant", true);
				}
				
			} 	
			if (countVaccinesRandom > 0) {
				Set<String> idsVaccinated = new HashSet<String>(countVaccinesRandom); 
				ColtRandomGenerator random = new ColtRandomGenerator();
				while (idsVaccinated.size() < countVaccinesRandom) {
					// pick up a random vertex
					String candidate = graph.getVertex(random.nextIntBetween(0, (int)graph.getVerticesCount()-1));
					idsVaccinated.add(candidate);
				}
				for (String vertexId: idsVaccinated) {
					
					graph.setVertexAttribute(vertexId, "presistant", true);
				}
				messages.infoUser("will vaccinate "+idsVaccinated.size()+" nodes randomly: "+idsVaccinated, getClass());

				
			} 
			
			// count the individual that are resistant before the simulation
			int totalResistanceBeginning = 0;
			for (String vertexId: graph.getVertices()) {
				if ((Boolean)graph.getVertexAttributeValue(vertexId, "presistant"))
					totalResistanceBeginning++;
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
	
			final File fileModel = NetlogoUtils.findFileInPlugin("ressources/models/Virus on a Network vaccines.nlogo");
				
			// run the model
			Map<String,Object> result = null;
			if (openGui) {
				GLLogger.warnUser("once open, Netlogo cannot be closed. It will close itself when you close Netlogo. Sorry.", getClass());
				result = RunNetlogoModel.runNetlogoModelGraphical(
						messages, 
						fileModel.getAbsolutePath(), 
						inputs, 
						requiredOutputs,
						maxStep,
						progress
						);
			} else {
				result = RunNetlogoModel.runNetlogoModelHeadless(
					messages, 
					fileModel.getAbsolutePath(), 
					inputs, 
					requiredOutputs,
					maxStep,
					progress
					);
				fileNetwork.delete();
			}
			// transmit results
			Double susceptibleAtEndOfSimulation = (Double)result.get("measure-susceptible");
			
			// if we had at the beginning 1/3 of the population vaccined
			// then the ideal result is to have at the end 2/3 of the population susceptible
			// because at the end 1/3 + 2/3 of the population were saved
			double protectedTotal = susceptibleAtEndOfSimulation +((double)totalResistanceBeginning)*100/graph.getVerticesCount();
			
			res.setResult(SIRModelAlgo.OUTPUT_INFECTED, result.get("measure-infected"));
			res.setResult(SIRModelAlgo.OUTPUT_SUSCEPTIBLE, susceptibleAtEndOfSimulation);
			res.setResult(SIRModelAlgo.OUTPUT_RESISTANT, result.get("measure-resistant"));
			res.setResult(SIRModelAlgo.OUTPUT_DURATION, result.get("_duration"));

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
