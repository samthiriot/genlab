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
		public final IInputOutput<?> inputInstance;
		
		public ContextObjectCreation(IAlgoContainer container, IInputOutput<?> inputInstance) {
			super();
			this.container = container;
			this.inputInstance = inputInstance;
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
			if (inputInstance == null) {
				if (other.inputInstance != null)
					return false;
			} else if (!inputInstance.equals(other.inputInstance))
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
					+ ((inputInstance == null) ? 0 : inputInstance.hashCode());
			return result;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(); 
			sb.append("container:").append(container.getName());
			sb.append(", ");
			sb.append("input:").append(inputInstance.getName());
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
	 * Search an intuitive creation for this context. searched both in the container and among constants
	 * @param context
	 * @return
	 */
	protected static ProposalObjectCreation searchForAnAutoInputForContext(ContextObjectCreation context) {

		GLLogger.debugTech("searching an intuitive input for context: "+context, IntuitiveObjectCreation.class);
		
		Map<IAlgo,Integer> proposals = new HashMap<IAlgo, Integer>(100);
				
		// let the container propose intuitive inputs
		// in some way, it is a "context"
		proposals.putAll(context.container.recommandAlgosContained());
		
		// load compliant constants
		IFlowType<?> searchedType = context.inputInstance.getType();
		for (IConstantAlgo algo: ExistingAlgos.getExistingAlgos().getConstantAlgos()) {
			if (searchedType.compliantWith(algo.getConstantOuput().getType())) {
				// add it to the list
				proposals.put(algo, algo.getPriorityForIntuitiveCreation());
			}
		}

		// now select only the compliant ones
		{
			Iterator<Map.Entry<IAlgo,Integer>> itAlgo = proposals.entrySet().iterator();
			while (itAlgo.hasNext()) {
				Map.Entry<IAlgo,Integer> current = itAlgo.next();
			
				final IAlgo algo = current.getKey();
				
				// TODO allow the automatic creation of algos with more than 1 output ?
				// reject algos with several outputs
				if (algo.getOuputs().size() != 1)
					itAlgo.remove();

				IInputOutput<?> output = algo.getOuputs().iterator().next();
				
				// reject the algo when the output is not relevant
				if (!searchedType.compliantWith(output.getType()))
					itAlgo.remove();
					
			}
		
		}
		
		// now retain the best one !
		Set<IAlgo> betterAlgos = new HashSet<IAlgo>();
		Integer higherPrority = 0;
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
		
		return new ProposalObjectCreation(bestAlgo, bestAlgo.getOuputs().iterator().next());
		
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
	
	
}
