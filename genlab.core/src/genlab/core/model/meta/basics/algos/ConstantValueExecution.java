package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecutionOneshot;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IInputOutput;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * For its execution, a constant value takes as a parameter the output to update, the value to output. 
 * 
 * @author Samuel Thiriot
 *
 * @param <JavaType>
 */
@SuppressWarnings("serial")
public class ConstantValueExecution<JavaType> 
										extends AbstractAlgoExecutionOneshot 
										implements IAlgoExecutionOneshot
										//,
										//IAlgoExecutionRemotable, Externalizable 
										 {

	
	protected JavaType value = null;
	
	public ConstantValueExecution(
			IExecution exec,
			IAlgoInstance algoInst,
			JavaType value
			) {
		super(
				exec,
				algoInst, 
				new ComputationProgressWithSteps()
				);
		this.value = value;
		
		// a constant is always ready
		progress.setComputationState(ComputationState.READY);

	}

	@Override
	public void run() {

		// notify of the start of the task
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		// define result
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		IInputOutput<JavaType> output = ((ConstantValue<JavaType>)algoInst.getAlgo()).getOutput(); 
		result.setResult(
				algoInst.getOutputInstanceForOutput(output), 
				value
				);

		// and, of the end.
		setResult(result);
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}
	
	@Override
	public boolean isCostless() {
		return true;
	}

	@Override
	public void kill() {
		// nothing to do (not a long task)
		
	}

	@Override
	public void cancel() {
		// nothing to do (not a long task)
	}

	@Override
	public String getName() {
		return "execute \""+algoInst.getName()+"\"";
	}

	@Override
	public long getTimeout() {
		return 500;
	}

	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {

		throw new ProgramException("received an input for a constant, which has obviously no input. oops, this should never happen.");
	}


	/**
	 * For serialization only
	 */
	public ConstantValueExecution() {}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(value);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		value = (JavaType) in.readObject();
	}


}
