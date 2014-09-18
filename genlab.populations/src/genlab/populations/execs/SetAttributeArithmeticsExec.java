package genlab.populations.execs;

import genlab.arithmetics.IExpressionsParser;
import genlab.arithmetics.ParserFactory;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.populations.algos.SetAttributeArithmeticsAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SetAttributeArithmeticsExec extends AbstractAlgoExecutionOneshot {

	public SetAttributeArithmeticsExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);

		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		// retrieve inputs
		// ... population to update
		final IPopulation inputPopulation = (IPopulation)getInputValueForInput(SetAttributeArithmeticsAlgo.INPUT_POPULATION);
		// ... agent type name
		final String entityTypeName = (String)getInputValueForInput(SetAttributeArithmeticsAlgo.INPUT_TYPENAME);
		// ... attribute name
		final String attributeName = (String)getInputValueForInput(SetAttributeArithmeticsAlgo.INPUT_ATTRIBUTENAME);		
		// ... min/max
		final String formula = (String)algoInst.getValueForParameter(SetAttributeArithmeticsAlgo.PARAM_FORMULA);
		
		// post processing of inputs
		final IAgentType type = inputPopulation.getPopulationDescription().getAgentTypeForName(entityTypeName);
		if (type == null) {
			dieExecutionWithMessage("the type "+entityTypeName+" does not exists; please correct the parameter");
			return;
		}
		final Attribute attribute = type.getAttributeForId(attributeName);
		if (attribute == null) {
			dieExecutionWithMessage("the attribute "+attributeName+" does not exists for entity type "+entityTypeName+"; please correct the parameter");
			return;
		}
		final IPopulation outPopulation = inputPopulation.clonePopulation();
		
		// TODO initial seed ?!!!
		
		try {
			Collection<IAgent> toUpdate = outPopulation.getAgents(type);
			
			IExpressionsParser ep = ParserFactory.getDefaultExpressionParser();
			
			if (toUpdate.size() > 0) {
			
	
				progress.setProgressTotal(toUpdate.size());
				
				// start actual processing
				// TODO check if already defined !!!
				int done = 0;
				Map<String,Object> params = new HashMap<String, Object>();
				for (IAgent a: toUpdate) {
					
					// set variables
					params.clear();
					for(Attribute at: type.getAllAttributes()) {
						Object atV = a.getValueForAttribute(at);
						
						if (atV != null)
							params.put(at.getID(), atV);
					}
					
					Object computed = ep.evaluate(formula, messages, params);
					a.setValueForAttribute(attribute, computed);
					
					done ++;
					
					if (done % 21 == 0) {
						progress.setProgressMade(done);
					}
				}
				
			}
			
		} catch (RuntimeException e) {
			dieExecutionWithMessage("error during the mathematical interpretation: "+e.getMessage(), e);
			return;
		}
		
		// TODO inform of number of replace !
		
		result.setResult(SetAttributeArithmeticsAlgo.OUTPUT_POPULATION, outPopulation);
		
		progress.setComputationState(ComputationState.FINISHED_OK);

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
