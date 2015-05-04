package genlab.server;

import genlab.core.GenLab;
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
		
		// start the server
		GenlabComputationServer.getSingleton().startServer();
		
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
