package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractAlgoExecutionOneshot 
									extends AbstractAlgoExecution
									implements IAlgoExecutionOneshot {

	
	protected Set<IInputOutputInstance> inputsNotAvailable = null;

	
	public AbstractAlgoExecutionOneshot(IExecution exec,
			IAlgoInstance algoInst, IComputationProgress progress) {
		super(exec, algoInst, progress);
		
		// at the very beginning, all the inputs are waiting for data
		inputsNotAvailable = new HashSet<IInputOutputInstance>(algoInst.getInputInstances());
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
	}


	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		
		// ignore all 
		if (progress.getComputationState() != ComputationState.WAITING_DEPENDENCY)
			return;
		
		if (to.getMeta().acceptsMultipleInputs()) {
			// this input accepts / expects several connection; so we have to check for all these connections !
			
			boolean allConnectionsProvidedValue = true;
			
			for (IConnectionExecution c: input2connection.get(to)) {
				if (c.getValue() == null) {
					allConnectionsProvidedValue = false;
					break;
				}
			}
			
			if (allConnectionsProvidedValue)
				inputsNotAvailable.remove(to);
			
		} else
			// this input only accepts one connection; so we can assume it is satisfied :-)
			inputsNotAvailable.remove(to);
		
		// maybe now we have all the required inputs ?
		if (inputsNotAvailable.isEmpty()) {
			exec.getListOfMessages().traceTech("all inputs are available, now ready to run !", getClass());
			progress.setComputationState(ComputationState.READY);
		}
			
	}
	
	@Override
	public void reset() {
		
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		inputsNotAvailable.clear();
		inputsNotAvailable.addAll(algoInst.getInputInstances());

	}

	@Override
	public void clean() {
		
		// clean local data
		inputsNotAvailable.clear();
		inputsNotAvailable = null;
		
		// super clean
		super.clean();
	}

}
