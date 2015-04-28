package genlab.core.exec.server;

import genlab.core.model.exec.IAlgoExecution;

import java.rmi.Remote;

public interface IGenlabComputationServer extends Remote {

	/**
	 * Pings the server with a given timestamp, and return the timestamp of processing.
	 * Enables the verification of the server availability.
	 * @param timeSent
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public long ping(long timeSent) throws java.rmi.RemoteException;
	
	/**
	 * Asks this server to execute a task. Will return after queing the task locally.
	 * @param task
	 * @throws java.rmi.RemoteException
	 */
	public void executeTask(IAlgoExecution task) throws java.rmi.RemoteException;
	
	
}
