package genlab.core.model.exec;

import genlab.core.model.instance.IInputOutputInstance;


/**
 * 
 * Tags an execution which will "reduce" a parallel algo; so it will receive successively several 
 * results. 
 * 
 * During the execution: as soon as created, the instance will be able to receive information in inputs. 
 * So it should really do all required inits during its creation, or it may detect the first arrival of information
 * and init at this time. 
 * 
 * Each input may receive information several times, with multithreading issues.
 * 
 * 
 * Then, once all parents are terminated, the execution will be notified of the actual end of the execution.
 * At this time, and at this time only, it should shift to status "ready"; its execution may be used for 
 * post processing, to check results and so on.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IReduceAlgoExecution extends IAlgoExecution {

	/**
	 * receive a value for a given input. May be called in a concurrent way.
	 * @param inputInstance
	 * @param value
	 */
	public void receiveInput(IAlgoExecution executionRun, IConnectionExecution connectionExec, Object value);
	
	/**
	 * Signals that an execution supervisor contains tasks which could send data
	 * @param supervisor
	 */
	public void signalIncomingSupervisor(AbstractContainerExecutionSupervisor supervisor);

	/**
	 * When a supervisor knows there is no more data to be send, it call this signal.
	 * @param supervisor
	 */
	public void signalEndOfTasksForSupervisor(AbstractContainerExecutionSupervisor supervisor, ComputationState state);
	
}