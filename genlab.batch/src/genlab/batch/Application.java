package genlab.batch;

import genlab.core.GenLab;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.GenlabExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.UserMachineInteractionUtils;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	
	protected void die(String error) {
		System.err.println();
		System.err.println(error);
	}
	
	protected void runWorkflow(String workflowFile) {
		
		// check the file exists
		File fileWorkflow = new File(workflowFile);
		if (!fileWorkflow.exists())
			die("this file does not exists: "+workflowFile);
		if (!fileWorkflow.canRead())
			die("this file can not be readen: "+workflowFile);
		
		try {
			
			// find project
			IGenlabProject project = GenlabPersistence.getPersistence().searchProjectForFile(fileWorkflow.getAbsolutePath());
			String relativeWorkflowFile = fileWorkflow.getAbsolutePath();
			if (!relativeWorkflowFile.startsWith(project.getBaseDirectory()))
				die("wrong path; internal error");
			relativeWorkflowFile = relativeWorkflowFile.substring(project.getBaseDirectory().length());
			
			IGenlabWorkflowInstance workflow = GenlabPersistence.getPersistence().readWorkflow(project, relativeWorkflowFile);
			
			IComputationProgress progress = GenlabExecution.runBlocking(workflow, true);
			switch (progress.getComputationState()) {
				case FINISHED_CANCEL:
					System.out.println("computation was canceled");
					break;
				case FINISHED_FAILURE:
					System.out.println("computation failed");
					break;
				case FINISHED_OK:
					System.out.println("computation finished in "+UserMachineInteractionUtils.getHumanReadableTimeRepresentation(progress.getDurationMs()));
					break;
				default:
					die("unknown state at the end of computations: "+progress.getComputationState());
			}
			
			
		} catch (WrongParametersException e) {
			die("error while reading the project");
		} 
		//GenlabPersistence.getPersistence().readWorkflow(project, relativeFilename)
	}
	
	protected void printUsage() {
		System.out.println("Genlab headless: usage");
		System.out.println("... still to be defined (TODO)"); // TODO command line ? script ?
		System.out.println("    java -Dosgi.requiredJavaVersion=1.5 -Dhelp.lucene.tokenizer=standard -XX:MaxPermSize=256m -Xms40m -Xmx512m -Dfile.encoding=UTF-8 -classpath /local00/home/B12772/opt/eclipseJuno/plugins/org.eclipse.equinox.launcher_1.3.0.v20120522-1813.jar org.eclipse.equinox.launcher.Main -launcher /local00/home/B12772/opt/eclipseJuno/eclipse -product genlab.batch.genlab_batch -os linux -ws gtk -arch x86_64 -nl fr_FR -consoleLog  <path to the workflow file>");
		System.out.println();

	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		
		// init
		System.out.println("initialization of genlab...");
		GenLab.getVersionString();
		System.out.println();
		
		GenlabPersistence.getPersistence().autoloadAllWorkflows = false;
		ListOfMessages.RELAY_TO_LOG4J = true;
		Logger.getRoot().setLevel(Level.INFO);
		
		System.out.print("Genlab ");
		System.out.print(GenLab.getVersionString());
		System.out.println();
		System.out.println();
		
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
		
		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		// nothing to do
	}
}
