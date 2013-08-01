package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;

import java.util.Arrays;

public class ConstantFilledTableGeneratorExec extends
		AbstractTableGeneratorExec {

	protected Object constantValue = null;
	
	public ConstantFilledTableGeneratorExec(IExecution exec,
			IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	@Override
	protected Object[] generateValues(int count) {

		Object[] res = new Object[count];
		Arrays.fill(res, constantValue);
		return res;
	}
	
	
	@Override
	protected void initRun() {
		// retrieve the value from the parameters
		constantValue = algoInst.getValueForParameter(
				(
						(ConstantFilledTableGenerator)(
								algoInst.getAlgo()
								)
						).getParametersForConstantValue().getId());	
	}

}
