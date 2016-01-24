package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * Class of algorithm executions which may be either oneshot, or reduce, depending to the context. 
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractAlgoExecutionOneshotOrReduce 
									extends AbstractAlgoExecution
									implements IAlgoExecutionOneshot, IReduceAlgoExecution {

	
	protected Set<IInputOutputInstance> inputsNotAvailable = null;

	
	public AbstractAlgoExecutionOneshotOrReduce(IExecution exec,
			IAlgoInstance algoInst, IComputationProgress progress) {
		super(exec, algoInst, progress);
		
		// at the very beginning, the inputs that don't come from reducing connections do expect answers
		inputsNotAvailable = new HashSet<IInputOutputInstance>();
		for (IInputOutputInstance inputInstance: algoInst.getInputInstances()) {
			for (IConnectionExecution c: this.getConnectionsForInput(inputInstance)) {
				if (c instanceof ConnectionExecFromIterationToReduce)
					// skip reduction links
					continue;
				inputsNotAvailable.add(inputInstance);
			}
		}
		
		if (inputsNotAvailable.isEmpty())
			progress.setComputationState(ComputationState.READY);
		else	
			progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		
		
	} 


	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		
		synchronized (inputsNotAvailable) {
			
		
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
			
		} // syncrhonized
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
		if (inputsNotAvailable != null)
		inputsNotAvailable.clear();
		inputsNotAvailable = null;
		
		// super clean
		super.clean();
	}

}
