package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.IInputOutput;

/**
 * A constant value is a very simple algorithm that has a value as a parameter, no input values,
 * and one unique output.
 *  
 * @author Samuel Thiriot
 *
 * @param <JavaType>
 */
public class ConstantValue<JavaType> extends BasicAlgo {

	private IInputOutput<JavaType> output;
	
	public final String paramId ;
			
	public ConstantValue(IFlowType<JavaType> type, IInputOutput<JavaType> output, String name, String desc) {
		super(
				name, 
				desc, 
				ExistingAlgoCategories.CONSTANTS.getId()
				);
		
		outputs.add(output);
		this.output = output;
		
		paramId = getId()+".params.value";
	}

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


}
