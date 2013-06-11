package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;

import java.util.Map;

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
	
	public ConstantValue(IFlowType<JavaType> type, IInputOutput<JavaType> output) {
		super(
				"constant", 
				"a constant", 
				"constants"
				);
		
		outputs.add(output);
		this.output = output;
		
	}

	@Override
	public IAlgoExecution createExec(
			IExecution exec,
			AlgoInstance algoInstance
			) {
		
		// this is a constant, so no input is expected
		if (!inputs.isEmpty())
			throw new ProgramException("a constant should not have inputs");
		
		return new ConstantValueExecution<JavaType>(
				exec,
				algoInstance,
				(JavaType)algoInstance.getValueForParameter("value")
				);
	}
	
	public IInputOutput<JavaType> getOutput() {
		return output;
	}

}
