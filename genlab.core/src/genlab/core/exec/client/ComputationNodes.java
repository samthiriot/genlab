package genlab.core.exec.client;

import genlab.core.exec.IRunner;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.HashMap;
import java.util.Map;

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
	

	private Map<String,DistantGenlabServerManager> host2serverManager = new HashMap<String, DistantGenlabServerManager>(50);
	
	private int distantPort = 25000;
	private String distantHost = "192.168.0.1";
	private boolean connectDistant = false;
			
	/**
	 * Returns (and creates if necessary) a default runner which runs locally 
	 * and/or distantly.
	 * @return
	 */
	public Runner getDefaultRunner() {
		if (runner == null) {
			messages.infoUser("starting a computation node with "+parameterLocalThreadsMax+" threads.", getClass());
			runner = new Runner(parameterLocalThreadsMax);
			runner.start();
		}
		return runner;
	}
	
	public void tryToAddDistantHost(String hostname, int port) {

		if (!connectDistant)
			return;
		
		final String keyServer = hostname+":"+port;

		synchronized (host2serverManager) {
			
			if (host2serverManager.containsKey(keyServer)) {
				messages.infoUser("server "+keyServer+" is already managed", getClass());
			} else {
				messages.infoUser("discovering server "+keyServer+"...", getClass());
				
				// add a local manager for this server
				DistantGenlabServerManager manager = new DistantGenlabServerManager(hostname, port);
				
				// keep it in memory
				host2serverManager.put(
						keyServer, 
						manager
						);
				
				// try to connect
				messages.infoUser("connecting server "+keyServer+"...", getClass());
				try {
					manager.connect();
				} catch (RuntimeException e) {
					messages.errorUser("unable to connect server "+keyServer+": "+e.getMessage(), getClass(), e);
					return;
				}
				
				// create threads to use to consume local tasks and delegate them to this server
				try {
					manager.createWorkerThreads(
							getDefaultRunner()							
							);
				} catch (RuntimeException e) {
					messages.errorUser("error while creating a thread for server "+keyServer+": "+e.getMessage(), getClass(), e);
					return;
				}
				
			}
		}
				
	}
	
	public void setParameterLocalThreadsMax(int max) {
		this.parameterLocalThreadsMax = max;
	}

	public int getParameterLocalThreadsMax() {
		return this.parameterLocalThreadsMax;
	}

	public void setParameterConnectServer(boolean boolean1) {
		connectDistant = boolean1;
		tryToAddDistantHost(distantHost, distantPort);
		// TODO what for stopping ? 
	}

	public void setParameterConnectServerHostname(String string) {
		distantHost = string;
	}

	public void setParameterConnectServerPort(int int1) {
		distantPort = int1;
	}
	
	
	
}
