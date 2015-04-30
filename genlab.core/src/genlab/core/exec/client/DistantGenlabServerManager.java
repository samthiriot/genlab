package genlab.core.exec.client;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.exec.server.IGenlabComputationServer;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class DistantGenlabServerManager {

	private final String hostname;
	private final int port;
	
	private IGenlabComputationServer server = null;
	
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	private int numberOfThreads = 0;
	
	/**
	 * The set of threads running on this server
	 */
	private Set<WorkingRunnerDistanceThread> threads = new HashSet<WorkingRunnerDistanceThread>();
	
	public DistantGenlabServerManager(String hostname, int port) {

		this.hostname = hostname;
		this.port = port;
		
	}
	
	public IGenlabComputationServer getDistantServer() {
		return server;
	}

	public void connect() {
	
		Registry registry;
		messages.infoUser("attempting to connect server "+hostname+":"+port, getClass());
		messages.infoTech("connecting to RMI registry...", getClass());
		
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
		} catch (RemoteException e) {
			messages.errorUser("unable to connect to server "+hostname+":"+port, getClass(), e);
			throw new RuntimeException(e);
		}

		server = null;
		
		// change class loader
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader()); 

		// retrieve the server on the registry
		try {
			server = (IGenlabComputationServer) registry.lookup(GenlabComputationServer.BOUNDING_NAME);
			
		} catch (AccessException e) {
			messages.errorUser("unable to connect to server "+hostname+":"+port+": "+e.getMessage(), getClass(), e);
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			messages.errorUser("unable to connect to server "+hostname+":"+port+": "+e.getMessage(), getClass(), e);
			throw new RuntimeException(e);
		} catch (NotBoundException e) {
			messages.errorUser("unable to connect to server "+hostname+":"+port+": "+e.getMessage(), getClass(), e);
			throw new RuntimeException(e);
		}  

		// check we can ping the server
		long timeSend = System.currentTimeMillis();
		try {
			long timeReceived = server.ping(timeSend);
			long diff = System.currentTimeMillis() - timeSend;
			System.out.println("ping in "+diff+" ms");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);

		}
				
		// retrieve informations
		try {
			numberOfThreads = server.getNumberTasksAccepted();
			messages.infoUser("server "+hostname+":"+port+" accepts up to "+numberOfThreads+" parallel tasks", getClass());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		doStatisticsPing();

		
	}
		
	public void doStatisticsPing() {
		
		final int total = 10;
		long cumulated = 0;
		
		for (int i=0; i<total; i++) {
			long timeSend = System.currentTimeMillis();
			try {
				long timeReceived = server.ping(timeSend);
				long diff = System.currentTimeMillis() - timeSend;
				cumulated += diff;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
	
			}
		}
		messages.infoTech("delay between us and server "+hostname+":"+port+": "+(cumulated/total)+" ms", getClass());

	}


	public void createWorkerThreads(BlockingQueue<IAlgoExecutionRemotable> mainQueue, BlockingQueue<IAlgoExecution> backupQueue) {
		
		
	}

	public void createWorkerThreads(Runner runner) {
		while (threads.size() < numberOfThreads) {
			
			try {
				messages.infoUser("starting working thread on server "+hostname+":"+port+" n°"+threads.size(), getClass());
				WorkingRunnerDistanceThread thread =  new WorkingRunnerDistanceThread(
						hostname+":"+port,
						"worker_distant_"+threads.size(), 
						runner.readyToComputeRemotable, 
						runner.readyToComputeWithThreads, 					
						this
						);
				runner.addRunnerDistant(thread);
				threads.add(thread);
				thread.start();
				
			} catch (RuntimeException e) {
				messages.warnUser("unable to create a distant worker thread", getClass());
			}
		}
	}
	
	
}
