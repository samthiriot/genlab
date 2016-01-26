package genlab.igraph.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.instance.IAlgoInstance;
import genlab.igraph.commons.IGraphLibImplementation;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public abstract class AbstractIGraphExec extends AbstractAlgoExecutionOneshot {


	public AbstractIGraphExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}

	/**
	 * for serialization only
	 */
	public AbstractIGraphExec(){}

	/**
	 * Based on the parameters and constraints of this algo (available only in only library),
	 * returns the library of interest.
	 */
	protected final IGraphLibImplementation getLibrary() {

		
		final Integer userParamIdx  = (Integer) algoInst.getValueForParameter(AbstractIGraphAlgo.PARAM_IMPLEMENTATION);
		final EIgraphImplementation userParam  = EIgraphImplementation.values()[userParamIdx];
		
		final EIgraphImplementation developerParam = ((AbstractIGraphAlgo)getAlgoInstance().getAlgo()).implementationAcceptedOnly;
				
		// no specific need: return the choice of the user
		if (developerParam == null)
			return IgraphLibFactory.getImplementation(userParam);
		else {
			// warn user
			if (
					(userParam == EIgraphImplementation.R_ONLY && developerParam == EIgraphImplementation.JNA_ONLY)
					||
					(userParam == EIgraphImplementation.JNA_ONLY && developerParam == EIgraphImplementation.R_ONLY)
					) {
				messages.warnUser("you asked for the Igraph implementation "+userParam.label+", but this algorithm is only available for "+developerParam.label+"; switching to this last one.", getClass());
			}
			return IgraphLibFactory.getImplementation(developerParam);
		}
	}
}
