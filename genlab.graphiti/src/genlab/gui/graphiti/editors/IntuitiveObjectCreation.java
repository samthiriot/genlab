package genlab.gui.graphiti.editors;

import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * In charge of providing intuitive ways to manipulate 
 * efficiently diagrams. For instance, automatic creation of constants.
 * 
 * TODO display menu if many possibilities ? 
 * TODO propose different steps for links creation, when input and outputs are not compliant.
 *  
 * @author Samuel Thiriot
 *
 */
public class IntuitiveObjectCreation {

	private IntuitiveObjectCreation() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @author Samuel Thiriot
	 */
	public static class ProposalObjectCreation {
		
		public final IAlgo algoToCreate;
		public final IInputOutput<?> ioToUse;
		
		public ProposalObjectCreation(IAlgo algoToCreate,
				IInputOutput<?> ioToUse) {
			super();
			this.algoToCreate = algoToCreate;
			this.ioToUse = ioToUse;
		}
		
		
	}
	
	/**
	 * 
	 * 
	 * @author Samuel Thiriot
	 */
	public static class ContextObjectCreation {
	
		public final IAlgoContainer container;
		public final IInputOutput<?> inputOrOutputInstance;
		
		public ContextObjectCreation(IAlgoContainer container, IInputOutput<?> inputInstance) {
			super();
			this.container = container;
			this.inputOrOutputInstance = inputInstance;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ContextObjectCreation other = (ContextObjectCreation) obj;
			if (container == null) {
				if (other.container != null)
					return false;
			} else if (!container.equals(other.container))
				return false;
			if (inputOrOutputInstance == null) {
				if (other.inputOrOutputInstance != null)
					return false;
			} else if (!inputOrOutputInstance.equals(other.inputOrOutputInstance))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((container == null) ? 0 : container.hashCode());
			result = prime * result
					+ ((inputOrOutputInstance == null) ? 0 : inputOrOutputInstance.hashCode());
			return result;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(); 
			sb.append("container:").append(container.getName());
			sb.append(", ");
			sb.append("input:").append(inputOrOutputInstance.getName());
			return sb.toString();
		}
		
		
	}
	
	
	/**
	 * Cache for the searching of an object creation for a given output
	 */
	protected static Map<ContextObjectCreation,ProposalObjectCreation> cacheOutput2ProposalAutoInput 
							= new HashMap<ContextObjectCreation, ProposalObjectCreation>(100);
	
	/**
	 * 
	 * Search an intuitive creation for this context. searched both in the container and among constants.
	 * Principles: we identify the algos which could be created in an automatic way: container proposals
	 * (for instance create container-specific elements), constants (with a priority between 0 and 99,
	 * the higher the stronger) and any other algorithm: those having 0 inputs have priority -1, those
	 * with two inputs priority -2, etc.
	 * @param context
	 * @return
	 */
	protected static ProposalObjectCreation searchForAnAutoOutputForContext(ContextObjectCreation context) {
		
		// TODO adapt for output
		GLLogger.debugTech("searching an intuitive input for context: "+context, IntuitiveObjectCreation.class);
		
		Map<IAlgo,Integer> proposals = new HashMap<IAlgo, Integer>(100);
				
		// let the container propose intuitive inputs
		// in some way, it is a "context"
		proposals.putAll(context.container.recommandAlgosContained());
		
		IFlowType<?> searchedType = context.inputOrOutputInstance.getType();

		// load all the algorithms which have only an output, not an input
		for (IAlgo algo: ExistingAlgos.getExistingAlgos().getAlgos()) {
			
			boolean oneOutputCompliant = false;
			for (IInputOutput<?> output : algo.getOuputs()) {
				if (searchedType.compliantWith(output.getType())) {
					oneOutputCompliant = true;
					break;
				}
			}
			
			if (oneOutputCompliant) {
				// add it to the list
//				proposals.put(algo, new Integer(-1 - algo.getInputs().size()));
				proposals.put(algo, algo.getPriorityForIntuitiveCreation());

			}
		}
		
		// now retain the best one !
		Set<IAlgo> betterAlgos = new HashSet<IAlgo>();
		Integer higherPrority = -100;
		for (Map.Entry<IAlgo,Integer> algo2priority : proposals.entrySet()) {
			
			final IAlgo algo = algo2priority.getKey();
			final Integer priority = algo2priority.getValue();
			
			if (priority == higherPrority) {
				betterAlgos.add(algo);
			} else if (priority > higherPrority) {
				betterAlgos.clear();
				betterAlgos.add(algo);
				higherPrority = priority;
			}
		}
		
		// and select only one
		IAlgo bestAlgo = null;
		if (betterAlgos.size() > 1) {
			GLLogger.warnTech("found several ("+betterAlgos.size()+") best solutions for intuitive creation, so the selection will be random :"+betterAlgos, IntuitiveObjectCreation.class);
			bestAlgo = betterAlgos.iterator().next();
		} else if (betterAlgos.size() == 1) {
			bestAlgo = betterAlgos.iterator().next();
			GLLogger.traceTech("found a solution for intuitive creation:"+bestAlgo, IntuitiveObjectCreation.class);
		} else {
			return null;
		}
		
		IInputOutput<?> io = null;
		for (IInputOutput<?> o : bestAlgo.getOuputs()) {
			if (context.inputOrOutputInstance.getType().compliantWith(o.getType())) {
				io = o;
				break;
			}
		}
		return new ProposalObjectCreation(bestAlgo, io);
		
	}
	
	protected static ProposalObjectCreation searchForAnAutoInputForContext(ContextObjectCreation context) {
		
		// TODO adapt for output
		GLLogger.debugTech("searching an intuitive input for context: "+context, IntuitiveObjectCreation.class);
		
		Map<IAlgo,Integer> proposals = new HashMap<IAlgo, Integer>(100);
				
		// let the container propose intuitive inputs
		// in some way, it is a "context"
		proposals.putAll(context.container.recommandAlgosContained());
		
		IFlowType<?> searchedType = context.inputOrOutputInstance.getType();

		// load all the algorithms which have only an input, not an output
		for (IAlgo algo: ExistingAlgos.getExistingAlgos().getAlgos()) {
			
			if (!algo.canBeContainedInto(context.container))
				continue;
			
			boolean oneInputCompliant = false;
			for (IInputOutput<?> input : algo.getInputs()) {
				if (input.getType().compliantWith(searchedType)) {
					oneInputCompliant = true;
					break;
				}
			}
			
			if (oneInputCompliant) {
				// add it to the list
				proposals.put(algo, algo.getPriorityForIntuitiveCreation());
			}
		}
		
		
		// now retain the best one !
		Set<IAlgo> betterAlgos = new HashSet<IAlgo>();
		Integer higherPrority = -100;
		for (Map.Entry<IAlgo,Integer> algo2priority : proposals.entrySet()) {
			
			final IAlgo algo = algo2priority.getKey();
			final Integer priority = algo2priority.getValue();
			
			if (priority == higherPrority) {
				betterAlgos.add(algo);
			} else if (priority > higherPrority) {
				betterAlgos.clear();
				betterAlgos.add(algo);
				higherPrority = priority;
			}
		}
		
		// and select only one
		IAlgo bestAlgo = null;
		if (betterAlgos.size() > 1) {
			GLLogger.warnTech("found several ("+betterAlgos.size()+") best solutions for intuitive creation, so the selection will be random :"+betterAlgos, IntuitiveObjectCreation.class);
			bestAlgo = betterAlgos.iterator().next();
		} else if (betterAlgos.size() == 1) {
			bestAlgo = betterAlgos.iterator().next();
			GLLogger.traceTech("found a solution for intuitive creation:"+bestAlgo, IntuitiveObjectCreation.class);
		} else {
			return null;
		}
		
		IInputOutput<?> io = null;
		for (IInputOutput<?> o : bestAlgo.getInputs()) {
			if (o.getType().compliantWith(context.inputOrOutputInstance.getType())) {
				io = o;
				break;
			}
		}
		return new ProposalObjectCreation(bestAlgo, io);
		
	}
	
	/**
	 * Provides a constant that provides an output of this type.
	 * @param output
	 * @return
	 */
	public static ProposalObjectCreation getAutoInputForOutput(IAlgoContainer algoContainer, IInputOutput<?> output) {
	
		ContextObjectCreation context = new ContextObjectCreation(algoContainer, output);
		
		ProposalObjectCreation res = cacheOutput2ProposalAutoInput.get(context);
		
		if (!cacheOutput2ProposalAutoInput.containsKey(context)) {
		
			// not in cache, search it.
			res = searchForAnAutoInputForContext(context);
			
			// store in cache
			cacheOutput2ProposalAutoInput.put(context, res);
			
		}
		
		return res;
	}
	

	/**
	 * Provides a constant that provides an output of this type.
	 * @param output
	 * @return
	 */
	public static ProposalObjectCreation getAutoOutputForInput(IAlgoContainer algoContainer, IInputOutput<?> input) {
	
		ContextObjectCreation context = new ContextObjectCreation(algoContainer, input);
		
		ProposalObjectCreation res = cacheOutput2ProposalAutoInput.get(context);
		
		if (!cacheOutput2ProposalAutoInput.containsKey(context)) {
		
			// not in cache, search it.
			res = searchForAnAutoOutputForContext(context);
			
			// store in cache
			cacheOutput2ProposalAutoInput.put(context, res);
			
		}
		
		return res;
	}
	
}
