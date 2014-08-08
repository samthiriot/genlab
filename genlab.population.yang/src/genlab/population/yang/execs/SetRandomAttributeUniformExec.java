package genlab.population.yang.execs;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.population.yang.YANGAlgos;
import genlab.population.yang.algos.CreatePopulationFromBNAlgo;
import genlab.population.yang.algos.SetRandomAttributeUniformAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;

public class SetRandomAttributeUniformExec extends AbstractAlgoExecutionOneshot {

	public SetRandomAttributeUniformExec(
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
		final IPopulation inputPopulation = (IPopulation)getInputValueForInput(SetRandomAttributeUniformAlgo.INPUT_POPULATION);

		// ... agent type
		final String entityTypeName = (String)algoInst.getValueForParameter(SetRandomAttributeUniformAlgo.PARAM_ENTITY_TYPE);		
		final String attributeName = (String)algoInst.getValueForParameter(SetRandomAttributeUniformAlgo.PARAM_ATTRIBUTE_NAME);		
		final Integer minValue = (Integer)algoInst.getValueForParameter(SetRandomAttributeUniformAlgo.PARAM_MIN);
		final Integer maxValue = (Integer)algoInst.getValueForParameter(SetRandomAttributeUniformAlgo.PARAM_MAX);
		
		// TODO links ? 
		final IAgentType type = inputPopulation.getPopulationDescription().getAgentTypeForName(entityTypeName);
		if (type == null) {
			String msg = "the type "+entityTypeName+" does not exists; please correct the parameter";
			messages.errorUser(msg, getClass());
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			throw new WrongParametersException(msg);
		}
		final Attribute attribute = type.getAttributeForId(attributeName);
		if (attribute == null) {
			String msg = "the attribute "+attribute+" does not exists for entity type "+entityTypeName+"; please correct the parameter";
			messages.errorUser(msg, getClass());
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			throw new WrongParametersException(msg);
		}
		
		
		// then start the generation
		YANGAlgos.setAttributeRandom(
				(ComputationProgressWithSteps) progress, 
				inputPopulation, 
				type, 
				messages, 
				attribute, 
				minValue, 
				maxValue
				);
		
		result.setResult(CreatePopulationFromBNAlgo.OUTPUT_POPULATION, inputPopulation);
		
		progress.setComputationState(ComputationState.FINISHED_OK);

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

}
