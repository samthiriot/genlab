package genlab.core.exec.server;

import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Permission;

/**
 * seems useless: -Djava.rmi.server.codebase=${workspace_loc}/ -Djava.security.policy=${workspace_loc}/genlab.core/remote/server/genlabServer.policy
 * 
 * @author B12772
 *
 */
public class GenlabComputationServer implements IGenlabComputationServer {

	private int port = 20000;
	private int numberProcessesMax = Runtime.getRuntime().availableProcessors();
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	public static final String BOUNDING_NAME = "GenlabComputationServer";
	
	/*
	public enum ServerState {
		DISCONNECTED,
		CONNECTING,
		CONNECTED,
		CONNECTION_PROBLEM,
		STOPPING;
	}
	*/
	
	public enum ServerState {
		STOPPED,
		STARTING,
		RUNNING,
		PROBLEM,
		STOPPING;
	}
	
	private ServerState state;
	
	private boolean shouldConnect = false;
	
	private Registry registry = null;
	private IGenlabComputationServer stub = null;
	
	
	public GenlabComputationServer() {
        super();
        state = ServerState.STOPPED;
	}
	
	private static GenlabComputationServer singleton = null;
	
	
	/**
	 * Returns or creates a server. If it is created, it is started deactivated.
	 * @return
	 */
	public static GenlabComputationServer getSingleton() {
		if (singleton == null) {
			singleton = new GenlabComputationServer();
		}
		return singleton;
	}
	

	/* TODO main !

	public static void main(String[] args) {
		try {
			start();
		} catch (Exception e) {
            System.err.println("GenlabComputationServer exception:");
            e.printStackTrace();
        }
	
    }
    	 */

	@Override
	public long ping(long timeSent) throws RemoteException {

		return System.currentTimeMillis();
	}

	@Override
	public DistantExecutionResult executeTask(IAlgoExecution task) throws RemoteException {
		
		// run
		messages.infoTech("running distant task "+task, getClass());
		try {
			// TODO change that; but required now
			task.getExecution().setExecutionForced(true);
			task.run();
			messages.debugTech("returning result for task "+task, getClass());
			// then retrieve results from the task
			DistantExecutionResult res = new DistantExecutionResult(
					task.getProgress().getComputationState(),
					task.getResult()
					);

			return res;
		} catch (RuntimeException e) {
			return new DistantExecutionResult(
					ComputationState.FINISHED_FAILURE, 
					task.getResult()
					);
		}
		
	}


	@Override
	public int getNumberTasksAccepted() throws RemoteException {
		return numberProcessesMax;
	}


	public void startServer() {
		
		messages.infoUser("starting a GenLab server on port "+port, getClass());
		state = ServerState.STARTING;
				
		//System.out.println(System.getProperty("java.class.path"));
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
        

			if (System.getSecurityManager() == null) {
				class MySecurityManager extends SecurityManager {
					public MySecurityManager() {}
					public void checkPermission() {}
					public void checkPermission(Permission perm) {}
					public void checkPermission(Permission perm, Object context) {}
				}
				System.setSecurityManager(new MySecurityManager());
	            //System.setSecurityManager(new SecurityManager());
	        }
			
			if (stub == null)
				stub = (IGenlabComputationServer) UnicastRemoteObject.exportObject(this, port);
	        
	        if (registry == null) {
	        	try {
	        		registry = LocateRegistry.createRegistry(port);
	        	} catch (ExportException e) {
	        		registry = LocateRegistry.getRegistry(port);
	        	}
	        } 
	        
	        
	        Thread.currentThread().setContextClassLoader(GenlabComputationServer.class.getClassLoader());

	        registry.rebind(BOUNDING_NAME, stub);
	        
	        // TODO display info so others can connect there.
	        state = ServerState.RUNNING;
	        messages.infoUser("GenLab server published as "+InetAddress.getLocalHost().getCanonicalHostName()+":"+port, getClass());
	        
		} catch (Exception e) {
			state = ServerState.PROBLEM;
			messages.errorTech("Unable to publish the GenLab server on port "+port+": "+e.getMessage(), getClass(), e);
			registry = null;
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
		
	}
	
	public void stopServer() {
		
		state = ServerState.STOPPING;
		
		try {
			// stop server
			registry.unbind(BOUNDING_NAME);
			
			// unbind us 
			// UnicastRemoteObject.unexportObject(stub, true);
			
			// Nota Bene: cannot stop a RMI registry. Can only unbind it. So let it be !
			// stop RMI registry
			//UnicastRemoteObject.unexportObject(registry, true);
			// just forget it
			registry = null;
					
			state = ServerState.STOPPED;			
	        messages.infoUser("GenLab server stopped", getClass());
			
		} catch (AccessException e) {
			state = ServerState.PROBLEM;
			messages.errorTech("Unable to stop the GenLab node on port "+port+": "+e.getMessage(), getClass(), e);
		} catch (RemoteException e) {
			state = ServerState.PROBLEM;
			messages.errorTech("Unable to stop the GenLab node on port "+port+": "+e.getMessage(), getClass(), e);
		} catch (NotBoundException e) {
			state = ServerState.PROBLEM;
			messages.errorTech("Unable to stop the GenLab node on port "+port+": "+e.getMessage(), getClass(), e);
		}
		
		
		
	}
	
	public void setParameterStartServer(boolean boolean1) {
		shouldConnect = boolean1; 
		if (shouldConnect) {
			switch (state) {
				case STOPPED:
				case PROBLEM:
					// let's connect
					startServer();
					break;
				case RUNNING:
					// do nothing !
					break;
				case STARTING:
				case STOPPING:
					// what should we do ? 
					// TODO
					break;
			}
		} else {
			switch (state) {
			case RUNNING:
				stopServer();
				break;
			case STOPPED:
				// nothing to do
				break;
			case PROBLEM:
				state = ServerState.STOPPED;
				break;
			case STARTING:
			case STOPPING:
				// what should we do ? 
				// TODO
				break;
		}
		}
	}


	public void setParameterStartServerPort(int int1) {
		if (int1 == this.port)
			return;
		this.port = int1;
		
		switch (state) {
		case STOPPED:
		case PROBLEM:
			// don nothing
			break;
		case RUNNING:
			// restart !
			stopServer();
			startServer();
			break;
		case STARTING:
		case STOPPING:
			// what should we do ? 
			// TODO
			break;
	}
	}

}
