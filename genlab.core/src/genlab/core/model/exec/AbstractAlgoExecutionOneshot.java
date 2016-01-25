package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractAlgoExecutionOneshot 
									extends AbstractAlgoExecution
									implements IAlgoExecutionOneshot, Externalizable {

	
	protected Set<IInputOutputInstance> inputsNotAvailable = null;

	
	public AbstractAlgoExecutionOneshot(IExecution exec,
			IAlgoInstance algoInst, IComputationProgress progress) {
		super(exec, algoInst, progress);

		// at the very beginning, all the inputs are waiting for data
		inputsNotAvailable = new HashSet<IInputOutputInstance>(algoInst.getInputInstances().size());
		for (IInputOutputInstance inputInstance : algoInst.getInputInstances()) {
			if (!inputInstance.getConnections().isEmpty())
				inputsNotAvailable.add(inputInstance);
		}
		
		initComputationState();
		
	} 
	
	/**
	 * Init the set of not available inputs, and the corresponding state (ready or waiting).
	 * Please override if relevant.
	 */
	protected void initComputationState() {
		
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
		if (inputsNotAvailable != null)
		inputsNotAvailable.clear();
		inputsNotAvailable = null;
		
		// super clean
		super.clean();
	}
	

	/**
	 * For serialization only
	 */
	public AbstractAlgoExecutionOneshot() {}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		{ 
			String[] l = new String[inputsNotAvailable.size()];
			int i = 0;
			for (IInputOutputInstance io: inputsNotAvailable) {
				l[i++] = io.getId();
			}
			out.writeObject(l);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		{ 
			String[] l = (String[]) in.readObject();
			inputsNotAvailable = new HashSet<IInputOutputInstance>(algoInst.getInputInstances().size());
			for (String i: l) {
				inputsNotAvailable.add(algoInst.getInputInstanceForInput(i));
			}
		}
		initComputationState();
	}

}
