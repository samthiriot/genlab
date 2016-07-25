package genlab.core.model.exec;

import java.util.Map;
import java.util.Map.Entry;

import genlab.core.exec.ICleanableTask;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.AbstractExplorationParameter;

public class ExplorationExecutionIteration extends AbstractContainerExecutionIteration implements ICleanableTask {

	protected final Map<String,Object> parameter2value;
	
	public ExplorationExecutionIteration(IExecution exec, IAlgoContainerInstance algoInst,
			IComputationProgress progress, Map<IConnection, Object> inputConnection2value,
			Map<IAlgoInstance, IAlgoExecution> instance2exec, String suffix, Map<String,Object> parameter2value) {
	
		super(exec, algoInst, progress, inputConnection2value, instance2exec, suffix);
		

		this.parameter2value = parameter2value;


		// now define the parameters
		// prepare the connections so the connections of parameters
		for (IAlgoInstance ai : algoInst.getChildren()) {
			if (ai.getAlgo() instanceof AbstractExplorationParameter) {
				final Object value = parameter2value.get(ai.getName());
				((ExplorationParameterExecution)instance2execForSubtasks.get(ai)).setValue(value);
			}
		}
	}
	

	@Override
	protected void initSubtasks() {
		super.initSubtasks();
		
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("iteration ");
		for (Entry<String,Object> entry : parameter2value.entrySet()) {
		    sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
		}
		return sb.toString();
	}
	
}
