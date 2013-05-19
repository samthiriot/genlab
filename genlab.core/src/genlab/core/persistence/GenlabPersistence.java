package genlab.core.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import genlab.basics.workflow.GenlabWorkflow;
import genlab.basics.workflow.WorkflowHooks;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

public class GenlabPersistence {

	private static GenlabPersistence singleton = new GenlabPersistence();
	
	public static GenlabPersistence getPersistence() {
		return singleton;
	}
	

	private XStream xstream; 
	
	private GenlabPersistence() {
		
		// init xtream
		GLLogger.debugTech("initializing xstream for persistence...", getClass());
		try {
			xstream = new XStream(new StaxDriver());
	
			xstream.alias("workflow", GenlabWorkflow.class);
			xstream.alias("algoinstance", AlgoInstance.class);

		} catch (Exception e) {
			GLLogger.errorTech("error when initializing xstream persitence.", getClass(), e);
		}
	}
	
	/**
	 * Saves a genlab workflow to a file.
	 * @param workflow
	 * @param file
	 */
	public void saveWorkflow(IGenlabWorkflow workflow) {
		
		// call hooks
		GLLogger.debugTech("preparing to save workflow "+workflow+", calling hooks...", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowSaving(workflow);
		
		
		// save to xml
		final String filename = workflow.getAbsolutePath();
		GLLogger.debugTech("saving workflow as XML to "+filename, getClass());
		try {
			xstream.toXML(
					workflow,
					new PrintStream(filename)
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the workflow "+workflow+" as XML to "+filename, getClass(), e);

		}
		
		GLLogger.debugTech("workflow "+workflow+" saved, calling hooks...", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowSaved(workflow);
		
		
	}
	
	public IGenlabWorkflow readWorkflow(IGenlabProject project, String relativeFilename) {
		
		// TODO set relative filename and project, which are transient.
		
		return null;
		
	}
	

}
