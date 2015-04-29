package genlab.core.exec.server;

import genlab.core.Activator;
import genlab.core.IGenlabPlugin;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.GLLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

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
	
	public static GenlabComputationServer referenceStrong = null;
	
	public static void buildPath() {
		
		Set<ClassLoader> classloaders = new HashSet<ClassLoader>();
		
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (int i=bundles.length -1 ; i>=0; i--) {
			Bundle bundle = bundles[i];
			try {
    			String activator =  (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
    			if (activator == null)
    				continue;
    			
    			Class activatorClass = bundle.loadClass(activator);
    			
    			if (IGenlabPlugin.class.isAssignableFrom(activatorClass)) {
    				
    				Method method = activatorClass.getMethod("getClassLoader");
					Object o = method.invoke(null);
    				ClassLoader cl = (ClassLoader)o;
    				
    				System.err.println("Detected classloader from plugin: "+o);
    				classloaders.add(cl);
    			}
    			
    			
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				GLLogger.warnTech("the plugin "+bundle+" activator does implement IGenlabPlugin, but does not declares static methods getClassLoader", GenlabComputationServer.class);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
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
		
		//System.out.println(System.getProperty("java.class.path"));
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
	        String name = "GenlabComputationServer";
	        //System.setSecurityManager(new RMISecurityManager());

	        GenlabComputationServer engine = new GenlabComputationServer();
	        referenceStrong = engine;
	        IGenlabComputationServer stub = (IGenlabComputationServer) UnicastRemoteObject.exportObject(engine, 0);
	        
	        Registry registry = null;
	        
	        if (registry == null) {
	        	registry = LocateRegistry.createRegistry(port);
	        }
	        // java.rmi.server.codebase 
	        
	        
	        //Thread.currentThread().setContextClassLoader(GenlabComputationServer.class.getClassLoader());
	        Thread.currentThread().setContextClassLoader(GenlabComputationServer.class.getClassLoader());

	        registry.rebind(name, stub);
	        System.out.println("GenlabComputationServer bound");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
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
	public DistantExecutionResult executeTask(IAlgoExecution task) throws RemoteException {
		
		// run
		System.err.println("execute on server "+task);
		try {
			// TODO change that; but required now
			task.getExecution().setExecutionForced(true);
			task.run();
			System.err.println("returning result for "+task);
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

}
