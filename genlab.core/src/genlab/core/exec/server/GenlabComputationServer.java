package genlab.core.exec.server;

import genlab.core.model.exec.IAlgoExecution;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Permission;

/**
 * seems useless: -Djava.rmi.server.codebase=${workspace_loc}/ -Djava.security.policy=${workspace_loc}/genlab.core/remote/server/genlabServer.policy
 * 
 * @author B12772
 *
 */
public class GenlabComputationServer implements IGenlabComputationServer {

	private static int port = 20000;
	
	public GenlabComputationServer() {
        super();
	}
	
	public static void start() {
		
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
        
		try {
	        String name = "GenlabComputationServer";
	        //System.setSecurityManager(new RMISecurityManager());

	        GenlabComputationServer engine = new GenlabComputationServer();
	        IGenlabComputationServer stub = (IGenlabComputationServer) UnicastRemoteObject.exportObject(engine, 0);
	        
	        Registry registry = null;
	        /*
	        try {
	        	registry = LocateRegistry.getRegistry(port);
	        } catch (RuntimeException e) {
	        	
	        }
	        */
	        if (registry == null) {
	        	registry = LocateRegistry.createRegistry(port);
	        }

	        registry.rebind(name, stub);
	        System.out.println("GenlabComputationServer bound");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		try {
			start();
		} catch (Exception e) {
            System.err.println("GenlabComputationServer exception:");
            e.printStackTrace();
        }
	
    }

	@Override
	public long ping(long timeSent) throws RemoteException {

		return System.currentTimeMillis();
	}

	@Override
	public void executeTask(IAlgoExecution task) throws RemoteException {
		
		// TODO Auto-generated method stub
		System.out.println("should compute task "+task);
		try {
			task.run();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}

}
