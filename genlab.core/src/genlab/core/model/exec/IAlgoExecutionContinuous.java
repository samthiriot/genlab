package genlab.core.model.exec;


/**
 * Tags the algo executions which run in continuous mode: once initialized, they may receive
 * several successive versions of an input. 
 * 
 * @author Samuel Thiriot
 *
 */
public interface IAlgoExecutionContinuous extends IAlgoExecution {

	/**
	 * receive a value for a given input. May be called in a concurrent way.
	 * @param inputInstance
	 * @param value
	 */
	public void receiveInputContinuous(IAlgoExecution continuousProducer, Object keyWave, IConnectionExecution connectionExec, Object value);
	
	
}
