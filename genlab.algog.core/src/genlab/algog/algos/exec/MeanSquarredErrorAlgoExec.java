package genlab.algog.algos.exec;

import genlab.algog.algos.instance.MeanSquaredErrorAlgoInstance;
import genlab.algog.algos.meta.MeanSquaredErrorAlgo;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IConnection;

import java.util.Map;


public class MeanSquarredErrorAlgoExec extends AbstractAlgoExecutionOneshot {

	private final MeanSquaredErrorAlgoInstance algoInst;
	
	public MeanSquarredErrorAlgoExec(IExecution exec, MeanSquaredErrorAlgoInstance algoInst) {
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
		
		progress.setComputationState(ComputationState.STARTED);

		
		
		try {

			Map<IConnection,Object> connection2referenceValue = algoInst.connection2parameterValue();

			Map<IConnection,Object> receivedValues = getInputValuesForInput(MeanSquaredErrorAlgo.INTPUT_VALUES);

			if (connection2referenceValue.size() != receivedValues.size())
				throw new ProgramException("detected an inconsistence between the algo parameters and the actual computation");
			
			int count = 0;
			double total = 0.0;
			
			for (IConnection cIn: connection2referenceValue.keySet()) {
				
				Number referenceValue = (Number)connection2referenceValue.get(cIn);
				Number receivedValue = (Number)receivedValues.get(cIn);
				
				IConnectionExecution cEx = getExecutableConnectionForConnection(cIn);
						
				total += Math.pow(referenceValue.doubleValue()-receivedValue.doubleValue(), 2);
				
				count ++;
				
			}
			
			double computed = total / count;

			ComputationResult res = new ComputationResult(algoInst, progress, messages);
			res.setResult(MeanSquaredErrorAlgo.OUTPUT_MEANSQUAREDERROR, computed);
			setResult(res);
			
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			messages.errorTech("an error occured in MeanSquarredErrorAlgoExec.run", getClass(), e);	
		}
	}

	@Override
	public void cancel() {
		
	}

	@Override
	public void kill() {
		
	}

}
