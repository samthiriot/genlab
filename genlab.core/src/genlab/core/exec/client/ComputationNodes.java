package genlab.core.exec.client;

import genlab.core.exec.client.DistantGenlabServerManager.ManagerState;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.security.Permission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entry point to access a computation node, that is something you can give 
 * tasks to for an actual run.
 *  
 * @author Samuel Thiriot
 *
 */
public class ComputationNodes {

	
	private static final ComputationNodes singleton = new ComputationNodes();
	
	public static final ComputationNodes getSingleton() {
		return singleton; 
	}

	/**
	 * The actual runner. 
	 */
	private Runner runner = null;
	
	/**
	 * Messages used internally to communicate with the user
	 */
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	/**
	 * Parameter: number of local threads.
	 */
	private int parameterLocalThreadsMax = Runtime.getRuntime().availableProcessors();
	
	/**
	 * Associates an host to its manager
	 */
	private Map<ServerHostPreference,DistantGenlabServerManager> host2serverManager = new HashMap<ServerHostPreference, DistantGenlabServerManager>(50);
	
	/**
	 * List of the hosts we should connect to
	 */
	private List<ServerHostPreference> paramRequiredHosts = new LinkedList<ServerHostPreference>();
	
	/**
	 * Defines, at the system scale, a security manager permissive enough for RMI
	 * (maybe even too permissive ?)
	 */
	public static void setUpPermissiveSecurityManager() {

		if (System.getSecurityManager() == null) {
			class MySecurityManager extends SecurityManager {
				public MySecurityManager() {}
				public void checkPermission() {}
				public void checkPermission(Permission perm) {}
				public void checkPermission(Permission perm, Object context) {}
			}
			System.setSecurityManager(new MySecurityManager());
	    }
	}

	/**
	 * Returns (and creates if necessary) a default runner which runs locally 
	 * and/or distantly.
	 * @return
	 */
	public Runner getDefaultRunner() {
		if (runner == null) {
			
			setUpPermissiveSecurityManager();

			messages.infoUser("starting a computation node with "+parameterLocalThreadsMax+" threads.", getClass());
			runner = new Runner(parameterLocalThreadsMax);
			runner.start();
			
		}
		return runner;
	}
	
	public void setParameterLocalThreadsMax(int max) {
		this.parameterLocalThreadsMax = max;
	}

	public int getParameterLocalThreadsMax() {
		return this.parameterLocalThreadsMax;
	}
	
	protected void manageDistantHost(final ServerHostPreference host) {
		
		DistantGenlabServerManager server = host2serverManager.get(host);
		
		if (server == null) {
			// not existing yet... let's create it !
			messages.infoUser("discovering server "+host+"...", getClass());
			
			// add a local manager for this server
			final DistantGenlabServerManager manager = new DistantGenlabServerManager(host.hostname, host.port);
			
			// keep it in memory
			host2serverManager.put(
					host, 
					manager
					);			
			
			(new Thread("connect_server_"+host) {
				public void run() {
					
					// try to connect
					messages.infoUser("connecting server "+host+"...", getClass());
					try {
						manager.connect();
					} catch (RuntimeException e) {
						messages.errorUser("unable to connect server "+host+": "+e.getMessage(), getClass(), e);
						return;
					}
					
					if (manager.getState() != ManagerState.CONNECTED)
						return;
					
					// create threads to use to consume local tasks and delegate them to this server
					try {
						manager.createWorkerThreads();
					} catch (RuntimeException e) {
						messages.errorUser("error while creating a thread for server "+host+": "+e.getMessage(), getClass(), e);
						return;
					}		
				}
			}).start();
			
		} 
		
	}
	
	protected void removeDistantHost(ServerHostPreference host) {

		DistantGenlabServerManager server = host2serverManager.get(host);

		if (server != null) {
			server.disconnect();
			this.host2serverManager.remove(host);
			this.paramRequiredHosts.remove(host);
		}
		
	}

	protected void reactToParameterChangeListOfHosts() {
		
		Set<ServerHostPreference> hostsToRemove = new HashSet<ServerHostPreference>();
		hostsToRemove.addAll(host2serverManager.keySet());
		
		// add the ones which are required
		for (ServerHostPreference hostExpected: this.paramRequiredHosts) {
			
			// host should be used
			if (hostExpected.active) {
				// this host is active
				// let's activate it or refresh it
				manageDistantHost(hostExpected);
				
				hostsToRemove.remove(hostExpected);
			} 
		 
		}
		
		// remove the ones not required anymore
		for (ServerHostPreference hostRemoved: hostsToRemove) {
			removeDistantHost(hostRemoved);
		}
		
	}
	
	public void setParameterListOfHosts(List<ServerHostPreference> hosts) {
		synchronized (hosts) {
			this.paramRequiredHosts = hosts;
			reactToParameterChangeListOfHosts();	
		}
	}
	
	
}
