package genlab.core.model.meta.basics.algos;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.IInputOutput;
import genlab.core.parameters.Parameter;

/**
 * A constant value is a very simple algorithm that has a value as a parameter, no input values,
 * and one unique output.
 *  
 * @author Samuel Thiriot
 *
 * @param <JavaType>
 */
public abstract class ConstantValue<JavaType> extends BasicAlgo implements IConstantAlgo {

	private IInputOutput<JavaType> output;
	
	public final String paramId ;
			
	public final Parameter<JavaType> parameterValue;
	
	public ConstantValue(IFlowType<JavaType> type, IInputOutput<JavaType> output, String name, String desc, String longHtmlDescription) {
		super(
				name, 
				desc, 
				null,
				ExistingAlgoCategories.CONSTANTS.getId(),
				null
				);

		paramId = getId()+".params.value";

		this.parameterValue = createConstantParameter();
		registerParameter(this.parameterValue);
		
		outputs.add(output);
		this.output = output;
		
	}

	
	/**
	 * should return the parameter used to tune the value of the constant
	 */
	protected abstract Parameter<JavaType> createConstantParameter();
	
	@Override
	public IAlgoExecution createExec(
			IExecution exec,
			AlgoInstance algoInstance
			) {
		
		// this is a constant, so no input is expected
		if (!inputs.isEmpty())
			throw new ProgramException("a constant should not have inputs");
		
		Object value = (JavaType)algoInstance.getValueForParameter(paramId);
		
		if (value == null)
			value = getParameter(paramId).getDefaultValue();
		
		return new ConstantValueExecution<JavaType>(
				exec,
				algoInstance,
				(JavaType)algoInstance.getValueForParameter(paramId)
				);
	}
	
	public IInputOutput<JavaType> getOutput() {
		return output;
	}

	@Override
	public IInputOutput getConstantOuput() {
		return output;
	}

	@Override
	public Parameter<?> getConstantParameter() {
		return parameterValue;
	}
	@Override
	public Bundle getBundle() {
		// TODO Auto-generated method stub
		return null;
	}

}
