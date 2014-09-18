package genlab.populations.execs;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.populations.algos.SetAttributeRandomDoubleAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.random.colt.ColtRandomGenerator;

import java.util.Collection;

public class SetAttributeRandomDoubleExec extends AbstractAlgoExecutionOneshot {

	public SetAttributeRandomDoubleExec(
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
		final IPopulation inputPopulation = (IPopulation)getInputValueForInput(SetAttributeRandomDoubleAlgo.INPUT_POPULATION);
		// ... agent type name
		final String entityTypeName = (String)getInputValueForInput(SetAttributeRandomDoubleAlgo.INPUT_TYPENAME);
		// ... attribute name
		final String attributeName = (String)getInputValueForInput(SetAttributeRandomDoubleAlgo.INPUT_ATTRIBUTENAME);		
		// ... min/max
		final Double minValue = (Double)algoInst.getValueForParameter(SetAttributeRandomDoubleAlgo.PARAM_MIN);
		final Double maxValue = (Double)algoInst.getValueForParameter(SetAttributeRandomDoubleAlgo.PARAM_MAX);
		
		// post processing of inputs
		if (minValue >= maxValue) {
			dieExecutionWithMessage("minimum value ("+minValue+") should be lower than maximum value ("+maxValue+")");
			return;
		}
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
		
		
		Collection<IAgent> toUpdate = outPopulation.getAgents(type);
		
		if (toUpdate.size() > 0) {
		
			ColtRandomGenerator gen = new ColtRandomGenerator();
		

			progress.setProgressTotal(toUpdate.size());
			
			// start actual processing
			// TODO check if already defined !!!
			int done = 0;
			for (IAgent a: toUpdate) {
				
				double randValue = gen.nextDoubleUniform(minValue, maxValue);
				a.setValueForAttribute(attribute, (Double)randValue);
				
				done ++;
				
				if (done % 21 == 0) {
					progress.setProgressMade(done);
				}
			}
			
		}
		
		// TODO inform of number of replace !
		
		result.setResult(SetAttributeRandomDoubleAlgo.OUTPUT_POPULATION, outPopulation);
		
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
