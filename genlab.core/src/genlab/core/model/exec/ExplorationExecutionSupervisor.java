package genlab.core.model.exec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.ExplorationAlgo;

/**
 * 
 * 
 * @author sam
 *
 */

public class ExplorationExecutionSupervisor extends AbstractContainerExecutionSupervisor {

	final Object lockIterations = new Object();
	int totalIterationsToDo = 0;
	int currentIteration = 0;
	boolean finished = false;
	
	int totalRepetitions = 1;
	int remainingRepetitions = 0;
	protected Map<String,List<Object>> parameter2values = null;
	protected Map<String,Integer> parameter2index = null;
	
	public ExplorationExecutionSupervisor(IExecution exec, IAlgoContainerInstance algoInst) {
		super(exec, algoInst);


		if (!(algoInst.getAlgo() instanceof ExplorationAlgo))
			throw new ProgramException("this execution should only be used for instances of loop algo");
		
		autoFinishWhenChildrenFinished = true;
		autoUpdateProgressFromChildren = true;
		
		totalRepetitions = (Integer)algoInst.getValueForParameter(ExplorationAlgo.PARAM_REPETITION);
		constructValuesToExplore();
		
		//progress.setComputationState(ComputationState.READY);
	}

	@Override
	protected void initFirstRun() {

	}

	@Override
	protected void startOfIteration() {
		synchronized (lockIterations) {
			currentIteration++;	
		}
	}

	@Override
	protected boolean shouldContinueRun() {
		synchronized (lockIterations) {
			return (currentIteration < totalIterationsToDo);
		}
	}
	

	@Override
	protected int evaluateRemainingSteps() {
		synchronized (lockIterations) {
			return totalIterationsToDo-currentIteration;
		}
	}

	@Override
	protected void endOfRun() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getSuffixForCurrentIteration() {
		return " "+currentIteration+"/"+totalIterationsToDo;

	}
	
	protected Object parseValue(String s) {
		
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			try {
				return Double.parseDouble(s);
			} catch (NumberFormatException e2) {
				return s.trim();
			}	
		}
	}

	protected List<Object> parseValues(String s) {
		List<Object> res = new LinkedList<>();
		
		if (s.contains("to")) {
			String[] splitFromTo = s.split("to");
			final String fromValueS = splitFromTo[0].trim().substring(1);
			String[] splitToBy = splitFromTo[1].split("by");
			final String toValueS = splitToBy[0].trim();
			String byValueS = splitToBy[1].trim();
			byValueS = byValueS.substring(0, byValueS.length()-1);
			
			try {
				final Number fromValue = (Number)parseValue(fromValueS);
				final Number toValue = (Number)parseValue(toValueS);
				final Number byValue = (Number)parseValue(byValueS);
	
				double currentValue = fromValue.doubleValue();
				int i = 0;
				while (currentValue < toValue.doubleValue()) {
					currentValue = fromValue.doubleValue() + i*byValue.doubleValue();
					res.add(currentValue);
					i++;
				}
			} catch (ClassCastException e) {
				throw new RuntimeException("expected <number> to <number> by <number>");
			}
			
		} else if (s.startsWith("[") && s.endsWith("]") ){
			for (String vS : s.substring(1, s.length()-1).split(",")) {
				res.add(parseValue(vS.trim()));
			}
		} else {
			res.add(parseValue(s));
		}
		return res;
		
	}
	/**
	 * parses somethings like { 
	 *  truc: 12, 
	 * 	rewiring: [0, 0.1, 0.9, 0.10],
	 *  size: [100 to 1000 by 100],
	 * }
	 * 
	 * @param s
	 */
	protected Map<String,List<Object>> parseParameter(String s) {
		
		
		Map<String,List<Object>> indicator2values = new LinkedHashMap<>();
		
		List<String> toks = new LinkedList<>();
		int openB = 0;
		int prev = 0;
		for (int i=0; i<s.length(); i++) {
			if (s.charAt(i) == '[') {
				openB += 1;
			} else if (s.charAt(i) == ']') {
				openB -= 1;
				// TODO if negative... error
			} else if (openB == 0 && s.charAt(i) == ',') {
				// that's a novel token ! 
				toks.add(s.substring(prev, i));
				prev = i+1;
			}
		}
		toks.add(s.substring(prev, s.length()));

		for (String t: toks) {
			
			String[] splitKV = t.split(":");
			if (splitKV.length != 2) {
				throw new RuntimeException("wrong format in \""+t+"\": expected <string>:<value>");
			}
			indicator2values.put(splitKV[0].trim(), parseValues(splitKV[1].trim()));
		}
		
		return indicator2values;
	}
	
	protected void constructValuesToExplore() {
		
		String parameterValue = (String) algoInst.getValueForParameter(ExplorationAlgo.PARAM_EXPLORATION);
		
		// this is the list that constructs all the combinations
		parameter2values = parseParameter(parameterValue);
		
		// total size 
		totalIterationsToDo = totalRepetitions;
		for (List<Object> l: parameter2values.values()) {
			totalIterationsToDo = totalIterationsToDo * l.size();
		}
		messages.infoUser("total iterations to do: "+totalIterationsToDo, getClass());
		
		// initialize iterator
		parameter2index = new HashMap<>();
		for (String k: parameter2values.keySet()) {
			parameter2index.put(k, 0);
		}
		currentIteration = 0;
		finished = false;
		remainingRepetitions = totalRepetitions;
		
	}
	
	protected Map<String,Object> generateNextValues() {
		
		synchronized (lockIterations) {
			Map<String,Object> res = new HashMap<>();
			
			// pile the current value
			for (String k: parameter2values.keySet()) {
				res.put(k, parameter2values.get(k).get(parameter2index.get(k)));			
			}

			// shift to next value
			remainingRepetitions--;
			
			if (remainingRepetitions <= 0) {
				
				remainingRepetitions = totalRepetitions;
				
				boolean incrementPossible = false;
				for (String k: parameter2values.keySet()) {
					
					int currentIdx = parameter2index.get(k) + 1;
					if (currentIdx == parameter2values.get(k).size()) {
						// oops, we are too far now
						// so now we reset this index
						parameter2index.put(k,0);
						// and from this point we continue to increment (as we don't break)
					} else {
						// we can increase this one; let's stop here
						parameter2index.put(k,currentIdx);
						incrementPossible = true;
						break;
					}
					
				}
				// detect end
				if (!incrementPossible) {
					finished = true;
				}
				
			}

			return res;	
		}
		
	}
	
	/**
	 * Returns an execution for one iteration (for instance, for a loop, the execution set of internal executions)
	 * Its inputs and outputs will be set by this one.
	 * @return
	 */
	protected IAlgoExecution createNextExecutionForOneIteration() {
		
		// prepare the data to send
		Map<IConnection,Object> inputConnection2value = new HashMap<IConnection, Object>();
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			inputConnection2value.putAll(getInputValuesForInput(c.getTo()));
			//Object value = getInputValueForInput(c.getTo());
			
			//inputConnection2value.put(c, value);
		}
		
		Map<String,Object> currentValues = generateNextValues();
		
		// create the container for the iteration
		messages.traceTech("creating the executable for this iteration...", getClass());
		ExplorationExecutionIteration resExecIteration = new ExplorationExecutionIteration(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps(), // TODO another progress ? 
				inputConnection2value, 
				instance2execOriginal, // instance2execForSubtasks,
				getSuffixForCurrentIteration(),
				currentValues
				);
		resExecIteration.autoFinishWhenChildrenFinished = true;
		resExecIteration.autoUpdateProgressFromChildren = true;
		resExecIteration.ignoreCancelFromChildren = false;
		resExecIteration.ignoreFailuresFromChildren = false;
		
		// now create the links to call this iteration
		messages.traceTech("init links for this iteration...", getClass());
		// note that iterations not only create input, but also output, connections (for connection between children and outside)
		resExecIteration.initInputs(instance2execForSubtasks);
				
		// set values
		messages.traceTech("defining the values to let the executable start...", getClass());
		{

			// remove from pending inputs the ones which are the inputs of the ref algo
			for (IInputOutputInstance input: algoInst.getInputInstances()) {
				
				resExecIteration.inputsNotAvailable.remove(input);	
			}
			
			// transmit data
			for (IConnection c: algoInst.getConnectionsComingFromOutside()) {
				for (IConnectionExecution cEx : resExecIteration.getOrCreateConnectionsForInput(c.getTo())) {
					Object value = inputConnection2value.get(c);
					messages.traceTech("defining value "+value+" for "+c, getClass());
					cEx.forceValue(value);
				}
			}
			resExecIteration.initComputationState();

			
		}
		
		return resExecIteration;
	}
	

}
