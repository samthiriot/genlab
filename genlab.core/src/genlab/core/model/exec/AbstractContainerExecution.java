package genlab.core.model.exec;

import java.util.HashSet;
import java.util.Set;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

public class AbstractContainerExecution extends AbstractAlgoExecution {

	private final IAlgoContainerInstance algoInst;
	
	public AbstractContainerExecution(
			IExecution exec, 
			IAlgoContainerInstance algoInst) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());

		this.algoInst = algoInst;
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {

		Set<IInputOutputInstance> inputs = new HashSet<IInputOutputInstance>();
		Set<IInputOutputInstance> outputs = new HashSet<IInputOutputInstance>();
		
		// identify input and output connections out of this algo inst
		for (IAlgoInstance aiChild : algoInst.getChildren()) {
			for (IInputOutputInstance input : aiChild.getInputInstances()) {
				for (IConnection c : input.getConnections()) {
					
					if (!algoInst.getChildren().contains(c.getFrom().getAlgoInstance())) {
						// this input is not connected to a children of this algo
						inputs.add(c.getFrom());
					}
				}
			}
			
			for (IInputOutputInstance output : aiChild.getOutputInstances()) {
				for (IConnection c : output.getConnections()) {
					
					if (!algoInst.getChildren().contains(c.getTo().getAlgoInstance())) {
						// this input is not connected to a children of this algo
						outputs.add(c.getFrom());
					}
				}
			}
		}
		
		GLLogger.debugTech("found inputs for this container: "+inputs, getClass());
		GLLogger.debugTech("found output for this container: "+outputs, getClass());
		
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
