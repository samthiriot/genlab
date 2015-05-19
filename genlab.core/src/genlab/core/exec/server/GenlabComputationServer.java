package genlab.core.exec.server;

import genlab.core.exec.client.ComputationNodes;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;

/**
 * seems useless: -Djava.rmi.server.codebase=${workspace_loc}/ -Djava.security.policy=${workspace_loc}/genlab.core/remote/server/genlabServer.policy
 * 
 * @author B12772
 *
 */
public class GenlabComputationServer implements IGenlabComputationServer {

	public static final int DEFAULT_PORT = 25555;
	
	private int port = DEFAULT_PORT;
	private String interfaceToBind = "automatic";
	private int numberProcessesMax = Runtime.getRuntime().availableProcessors();
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	public static final String BOUNDING_NAME = "GenlabComputationServer";
	
	
	
	public enum ServerState {
		STOPPED,
		STARTING,
		RUNNING,
		PROBLEM,
		STOPPING;
	}
	
	private ServerState state;
	
	private boolean shouldConnect = false;
	private String preferedInterface = "eth0";
	
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
			messages.infoTech("finished distant task "+task, getClass());
			// then retrieve results from the task
			DistantExecutionResult res = new DistantExecutionResult(
					task.getProgress().getComputationState(),
					task.getResult()
					);

			// stop the list of messages, so it's not continuing forever
			task.getExecution().getListOfMessages().stop();
			
			// suggest gc ?!
			System.gc();
			
			return res;
		} catch (RuntimeException e) {
			messages.errorTech("error while running distant task "+task.getName()+": "+e.getMessage(), getClass(), e);
			return new DistantExecutionResult(
					ComputationState.FINISHED_FAILURE, 
					task.getResult()
					);
		} catch (Error e) {
			messages.errorTech("error while running distant task "+task.getName()+": "+e.getMessage(), getClass(), e);
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
	
		
	protected InetAddress findExternalAddress() {
		
		InetAddress res = null;
		
		
		messages.infoUser("attempting to detect the external IP to be used...", getClass());
		try {
			for (NetworkInterface n: Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (n.isVirtual()) {
					messages.infoUser("interface "+n.getDisplayName()+" is virtual, ignoring it", getClass());
					continue;
				}
				if (!n.isUp()) {
					messages.infoUser("interface "+n.getDisplayName()+" is down, ignoring it", getClass());
					continue;
				}
				// TODO parameter for prefered interface
				//if (!n.getName().equals("eth0"))
				//	continue;
				for (InetAddress ip: Collections.list(n.getInetAddresses())) {
					// not use the local ones
					if (n.isLoopback() || ip.isLoopbackAddress()) {
						
						messages.infoUser("not using the local address "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
					} else if (!(ip instanceof Inet4Address)) {
						messages.infoUser("not using the non-IPv4 adresse "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
					} else {
						
						boolean reachable = false;
						try {
							reachable = ip.isReachable(500);
						} catch (IOException e) {
						}
						if (!reachable) {
							messages.infoUser("not using the unreachable address "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
						} else {
							
							if (res == null) {
								res = ip;
								messages.infoUser("will use valid external IP :"+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
							} else {
								// TODO message informatif
								messages.infoUser("this IP lools valid; we use the first one nevertheless: "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
							
							}
						}
					}
				}
			}
			

			if (res == null) {
				messages.warnUser("unable to find an external IP; this server will be only available from local computer", getClass());
				return InetAddress.getLocalHost();
				
			} else {
				return res;
			}
			
		} catch (SocketException e) {
			throw new RuntimeException("Unable to list local IP adresses", e);
		} catch (UnknownHostException e) {
			return null;
		}
		
	}
	

	protected InetAddress loadExternalAddress() {
		
		InetAddress res = null;
				
		messages.infoUser("attempting to detect the external IP to be used on interface "+this.interfaceToBind+"...", getClass());
		try {
			NetworkInterface n = NetworkInterface.getByName(this.interfaceToBind);
			if (n == null || !n.isUp()) {
				messages.warnUser("unable to user interface "+this.interfaceToBind+" on this machine; falling back to automatic detection.", getClass());
				return findExternalAddress();
			}
			
				
			for (InetAddress ip: Collections.list(n.getInetAddresses())) {
				// not use the local ones
				if (n.isLoopback() || ip.isLoopbackAddress()) {
					messages.infoUser("not using the local address "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
				} else if (!(ip instanceof Inet4Address)) {
					messages.infoUser("not using the non-IPv4 adresse "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
				} else {
					
					boolean reachable = false;
					try {
						reachable = ip.isReachable(500);
					} catch (IOException e) {
					}
					if (!reachable) {
						messages.infoUser("not using the unreachable address "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
					} else {
						
						if (res == null) {
							res = ip;
							messages.infoUser("will use valid external IP :"+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
						} else {
							// TODO message informatif
							messages.infoUser("this IP lools valid; we use the first one nevertheless: "+n.getDisplayName()+"/"+ip.getHostAddress(), getClass());
						
						}
					}
				}
			}
		

			if (res == null) {
				messages.warnUser("unable to find a valid IP on the user interface "+this.interfaceToBind+"; falling back to automatic detection.", getClass());
				return findExternalAddress();
				
			} else {
				return res;
			}
			
		} catch (SocketException e) {
			throw new RuntimeException("Unable to list local IP adresses", e);
		} 
		
	}


	public void startServer() {
		
		messages.infoUser("starting a GenLab server on port "+port, getClass());
		state = ServerState.STARTING;
				
		//System.out.println(System.getProperty("java.class.path"));
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
        
			ComputationNodes.setUpPermissiveSecurityManager();
			
			// define the external IP property
			InetAddress address = null;
			{
			
				// TODO !
				if (this.interfaceToBind.equals("automatic")) 
					address = findExternalAddress();
				else
					address = loadExternalAddress();
				//address = InetAddress.getLocalHost();
				System.setProperty("java.rmi.server.hostname", address.getHostName());
				//System.setProperty("java.rmi.server.hostname", "clau5ejl");
						
			}
			if (stub == null)
				stub = (IGenlabComputationServer) UnicastRemoteObject.exportObject(this, port);
	        
	        if (registry == null) {

		        // replace variable in user environment, in case it would not be done by equinox
		        {
			        String codeBaseBefore = System.getProperty("java.rmi.server.codebase");
			        String codeBaseAfter = codeBaseBefore.replaceAll("\\$LOCALAPPDATA", System.getProperty("user.dir"));
		        	if (!codeBaseAfter.equals(codeBaseBefore))
		        		System.setProperty("java.rmi.server.codebase",codeBaseAfter);
			        messages.infoTech("replaced rmi codebase: "+codeBaseAfter+" (was "+codeBaseBefore+")", getClass());
				}

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
	        messages.infoUser("GenLab server published as "+address.getCanonicalHostName()+":"+port+", or maybe "+InetAddress.getLocalHost().getCanonicalHostName()+":"+port, getClass());
	        
		} catch (Exception e) {
			state = ServerState.PROBLEM;
			messages.errorTech("Unable to publish the GenLab server on port "+port+": , or maybe "+e.getMessage(), getClass(), e);
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

	public void setParameterInterfaceToBind(String interfaceToBind) {
		if (interfaceToBind.equals(this.interfaceToBind))
			return;
		
		this.interfaceToBind = interfaceToBind;
		
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
