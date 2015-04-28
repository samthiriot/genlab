package genlab.core.exec.client;

import genlab.core.exec.server.IGenlabComputationServer;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;

public class WorkingRunnerDistanceThread extends Thread {

	
	private final BlockingQueue<IAlgoExecution> readyToCompute;
	private final ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	private final String host;
	private final int port;
	
	private IGenlabComputationServer server = null;
	
	public WorkingRunnerDistanceThread(String name, BlockingQueue<IAlgoExecution> readyToCompute, String host, int port) {
		super(name);
		
		// save parameters
		this.readyToCompute = readyToCompute;		
		this.host = host;
		this.port = port;
		
		// configure thread
		setDaemon(true);
		setPriority(MIN_PRIORITY);
		
		// attempt to connect
		connect();
	}

	private void connect() {
	
	
		Registry registry;
		System.out.println("client: get registry");

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		server = null;
		System.out.println("client: lookup GenLab server");
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader()); 

		try {
			server = (IGenlabComputationServer) registry.lookup("GenlabComputationServer");
			
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}  

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
		
		doStatisticsPing(server);
		
	}
		
	public static void doStatisticsPing(IGenlabComputationServer server) {
		
		final int total = 10;
		long cumulated = 0;
		
		for (int i=0; i<total; i++) {
			long timeSend = System.currentTimeMillis();
			try {
				long timeReceived = server.ping(timeSend);
				long diff = System.currentTimeMillis() - timeSend;
				System.out.println("ping "+i+": "+diff+" ms");
				cumulated += diff;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
	
			}
		}
		System.out.println("average: "+(cumulated/total)+" ms");
	}
	
	@Override
	public void run() {

		while (true) {
		
			IAlgoExecution exec = null;
			try {
				exec = readyToCompute.take();
			} catch (InterruptedException e) {
				messages.errorTech("catched an exception from the execution: "+e.getMessage(), getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				try {
					exec.getResult().getMessages().errorUser("computation died with an error: "+e.getMessage(), getClass(), e);
				} catch (NullPointerException e2) {
				}
			}
			// run the task
			messages.debugTech(getName()+" running task "+exec.getName()+" in server "+host+":"+port, getClass());
			try {
				server.executeTask(exec);
				//exec.run();
			} catch (RemoteException e) {
				messages.errorUser("task "+exec.getName()+" raised a distant error:"+e.getMessage(), getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				exec.getProgress().setException(e);
			} catch (Exception e) {
				messages.errorUser("task "+exec.getName()+" raised an error:"+e.getMessage(), getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				exec.getProgress().setException(e);
			} catch (OutOfMemoryError e) {
				messages.errorUser("no more memory while processing task "+exec.getName()+"; update the memory settings", getClass(), e);
				exec.getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
				exec.getProgress().setException(e);
			}
			messages.debugTech(getName()+" ran task: "+exec.getName(), getClass());
			
		}

	}
	
	

	
}
