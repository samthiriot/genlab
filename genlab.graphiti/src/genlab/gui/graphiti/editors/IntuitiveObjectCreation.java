package genlab.gui.graphiti.editors;

import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.GLLogger;

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
	 * Provides a constant that provides an output of this type.
	 * @param output
	 * @return
	 */
	public static ProposalObjectCreation getAutoInputForOutput(IInputOutput<?> output) {
	
		ProposalObjectCreation res = null;
		
		GLLogger.debugTech("searching a relevant constant for an output : "+output.getType(), IntuitiveObjectCreation.class);
		
		IFlowType<?> searchedType = output.getType();
		
		for (IConstantAlgo algo: ExistingAlgos.getExistingAlgos().getConstantAlgos()) {
			if (searchedType.compliantWith(algo.getConstantOuput().getType())) {
				// found !
				GLLogger.traceTech("found a constant that exports type "+searchedType+": "+algo, IntuitiveObjectCreation.class);
				res = new ProposalObjectCreation(algo, algo.getConstantOuput());
				break;
			}
		}
		
		return res;
	}
	
	
}
