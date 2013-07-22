package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.parameters.Parameter;

public abstract class ConstantFilledTableGenerator extends AbstractTableGenerator {

	public ConstantFilledTableGenerator(String name, String description) {
		super(name, description);
	}

	public abstract Parameter<?> getParametersForConstantValue();
	
	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new ConstantFilledTableGeneratorExec(execution, algoInstance);
	}
}
