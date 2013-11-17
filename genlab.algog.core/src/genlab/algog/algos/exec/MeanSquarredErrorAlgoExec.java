package genlab.algog.algos.exec;

import genlab.algog.algos.instance.MeanSquaredErrorAlgoInstance;
import genlab.algog.algos.meta.MeanSquaredErrorAlgo;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IConnection;

import java.util.HashMap;
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

	@SuppressWarnings("unused")
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
			
			// normalization
			/*
			Map<IConnection,Double> connection2normalization = new HashMap<IConnection, Double>(connection2referenceValue.size());
			for (IConnection cIn: connection2referenceValue.keySet()) {
				
				Number referenceValue = (Number)connection2referenceValue.get(cIn);
				
				connection2normalization.put(cIn, 1/referenceValue)
				total += Math.pow(referenceValue.doubleValue()-receivedValue.doubleValue(), 2);
				
				count ++;
				
			}
			*/
			
			// first identify the scale for each variable
			for (IConnection cIn: connection2referenceValue.keySet()) {
				
				Number referenceValue = (Number)connection2referenceValue.get(cIn);
				Number receivedValue = (Number)receivedValues.get(cIn);
				
				total += Math.pow(
						(referenceValue.doubleValue()-receivedValue.doubleValue())/referenceValue.doubleValue(), 
						2
						);
				
				//System.err.println(cIn.getFrom().getName()+": "+receivedValue);
				
				count ++;
				
			}
			
			double computed = Math.sqrt(total / count);
			//System.err.println("fitness: "+computed);
			
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
