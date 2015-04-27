package genlab.gui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IAlgoExecutionContinuous;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;

public abstract class AbstractOpenViewContinuousAlgoExec 	
									extends AbstractOpenViewAlgoExec 
									implements IAlgoExecutionContinuous {

	public AbstractOpenViewContinuousAlgoExec(IExecution exec,
			IAlgoInstance algoInst, String viewId) {
		super(exec, algoInst, viewId);
		// TODO Auto-generated constructor stub
	}


	protected abstract void setDataFromContinuousUpdate(IAlgoExecution continuousProducer,
			Object keyWave, IConnectionExecution connectionExec, Object value);

	

	@Override
	public void receiveInputContinuous(IAlgoExecution continuousProducer,
			Object keyWave, IConnectionExecution connectionExec, Object value) {

		setDataFromContinuousUpdate(continuousProducer, keyWave, connectionExec, value);

		if (!openDisplayIfNecessary())
			displayResultsAsync(theView);

	}


}
