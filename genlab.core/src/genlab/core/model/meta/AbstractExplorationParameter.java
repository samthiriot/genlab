package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ExplorationParameterExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public abstract class AbstractExplorationParameter extends BasicAlgo {

	public final InputOutput<?> output;
	
	public AbstractExplorationParameter(String name, String description, InputOutput<?> poutput) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.EXPLORATION, 
				null, 
				null
				);
		
		output = poutput;
		
		outputs.add(output);
	}

	public InputOutput<?> getExplorationOutput() {
		return output;
	}
	
	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new ExplorationParameterExecution(execution, algoInstance);
	}


	@Override
	public boolean canBeContainedInto(IAlgoContainer algoContainer) {
		// genes can only be contained into genetic exploration algos
		return (algoContainer instanceof ExplorationAlgo);
	}

	public abstract Object castValue(Object value);
	
}
