package genlab.server;

import java.net.NetworkInterface;
import java.net.SocketException;

import javax.management.RuntimeErrorException;

import genlab.core.GenLab;
import genlab.core.commons.ProgramException;
import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	private enum PARSED_OPTION {
		INTERFACE,
		PORT
	};
	
	protected void printUsage() {
	
		System.err.println("Usage: java <usual parameters> [-port [1000-65535]] [-interface eth0|wlan0|...]");
	}
	
	protected void parseArguments() {
	
		PARSED_OPTION optionParsed = null;
		
		for (String arg: Platform.getApplicationArgs()) {
			
			if (optionParsed != null) {
				switch (optionParsed) {
				case INTERFACE:
					try {
						String itf = arg;
						GLLogger.infoUser("decode parameter interface : "+itf, getClass());

						NetworkInterface n = NetworkInterface.getByName(itf);
						if (n == null) {
							GLLogger.errorUser("unable to use network interface "+itf +" which is not found on this machine", getClass());
							printUsage();
							throw new RuntimeException("Wrong parameter for the server");
						}
						if (!n.isUp()) {
							GLLogger.errorUser("unable to use network interface "+itf +" which is not up", getClass());
							printUsage();
							throw new RuntimeException("Wrong parameter for the server");
						}

						if (n.isLoopback()) {
							GLLogger.warnUser("the network interface "+itf+" might be local; the server will probably not be visible from other computers", getClass());
						}
						// parameters for starting a server
						GenlabComputationServer.getSingleton().setParameterInterfaceToBind(itf);
												
					} catch (Error e) {
						throw new RuntimeException("Error while decoding the interface parameter: "+e.getMessage(), e);
					} catch (SocketException e) {
						throw new RuntimeException("Error while decoding the interface parameter: "+e.getMessage(), e);
					}
					break;
				case PORT:
					try {
						int port = Integer.parseInt(arg);
						
						if ((port < 1000) || (port >= 65535)) {
							GLLogger.errorUser("not a valid part number: "+arg+"(should be an integer between 1000 and 65535)", getClass());
							printUsage();
							throw new RuntimeException("Wrong parameter for the server");
						}
						GenlabComputationServer.getSingleton().setParameterStartServerPort(port);
						
					} catch (NumberFormatException e) {
						GLLogger.errorUser("not a valid part number: "+arg+"(should be an integer)", getClass());
						printUsage();
						throw new RuntimeException("Wrong parameter for the server");
					}
					break;
				default:
					printUsage();
					throw new ProgramException("unknown constant "+arg);

				}
				
				optionParsed = null;
				
			} else {
				if (arg.equals("-port")) {
					optionParsed = PARSED_OPTION.PORT;
				} else if (arg.equals("-interface")) {
					optionParsed = PARSED_OPTION.INTERFACE;
				} else {
					printUsage();
					throw new RuntimeException("unexpected parameter: "+arg);
				}
			}
		}
	

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		
		// configure GenLab
		GenlabPersistence.getPersistence().autoloadAllWorkflows = false;
		
		// configure logs
		ListOfMessages.DEFAULT_RELAY_TO_LOG4J = true;
		BasicConfigurator.configure();
		Logger.getRoot().setLevel(Level.DEBUG);
	    

		System.out.print("Genlab ");
		System.out.print(GenLab.getVersionString());
		System.out.println();
		System.out.println();
		
		// parse command-line
		/*
		String[] args = Platform.getCommandLineArgs();
		String filenameWorkflow = null;
		for (int i=0; i<args.length; i++) {
			
			if (args[i].startsWith("-")) {
				i++; // pass this option and its value
				continue;
			}
			
			filenameWorkflow = args[i];
			break;
		}
		
		if (filenameWorkflow == null)
			printUsage();
		else
			runWorkflow(filenameWorkflow);
		*/

		// parse arguments
		parseArguments();
		
		// start the server
		GenlabComputationServer.getSingleton().setParameterStartServer(true);
		
		// wait
		while (true) {
			try {
				GLLogger.infoTech("sleeping", getClass());
				Thread.sleep(500000);
			} catch (InterruptedException e) {
				
			}
		}
		// TODO ? context.getArguments()
		
		//GLLogger.infoTech("closing", getClass());
		
		//return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		
		GenlabComputationServer.getSingleton().stopServer();
		
	}
	
}
