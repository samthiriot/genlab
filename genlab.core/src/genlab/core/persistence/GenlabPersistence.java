package genlab.core.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

public class GenlabPersistence extends AbstractPersistence {

	public static final String EXTENSION_WORKFLOW = ".glw"; // GenLabWorkflow
	
	public static final String XMLTAG_ID = "glid"; 

	public static final String XMLTAG_WORKFLOW = "workflow"; 
	public static final String XMLTAG_ALGOINSTANCE = "algo"; 
	public static final String XMLTAG_CONNECTIONINSTANCE = "connection"; 

	private static GenlabPersistence singleton = new GenlabPersistence();
	
	public static GenlabPersistence getPersistence() {
		return singleton;
	}
	
	private static Map<String, IGenlabWorkflowInstance> filename2workflow = new HashMap<String, IGenlabWorkflowInstance>();

	private String currentWorkflowAbsoluteName = null;

	private GenlabWorkflowInstance currentWorkflowInstance = null;

	/**
	 * if true, when a project is loaded, all its sub workflows are loaded (of use for GUI)
	 */
	public boolean autoloadAllWorkflows = true;
	
	public IGenlabWorkflowInstance getWorkflowForFilename(String filename) {
		IGenlabWorkflowInstance workflow = filename2workflow.get(filename);
		if (workflow == null) {
			GLLogger.warnTech(
					"was asked a workflow for file "+filename+", but it was not yet loaded :-(", 
					getClass()
					);
		}
		return workflow;
	}
	
	private GenlabPersistence() {
		
		super();
		
		// init xtream
		GLLogger.debugTech("initializing xstream for persistence...", getClass());
		try {
	
			xstream.alias(XMLTAG_WORKFLOW, GenlabWorkflowInstance.class);
			
			xstream.registerConverter(new WorkflowConverter());
			
			xstream.registerConverter(new FlowTypeConverter());
			
			xstream.alias(XMLTAG_CONNECTIONINSTANCE, Connection.class);
			xstream.registerConverter(new ConnectionConverter());

			//xstream.alias("inputoutput", InputOutput.class);
			//xstream.registerConverter(new InputOutputConverter());
			
			xstream.alias(XMLTAG_ALGOINSTANCE, AlgoInstance.class);
			xstream.registerConverter(new AlgoInstanceConverter());
			
		} catch (Exception e) {
			GLLogger.errorTech("error when initializing xstream persitence.", getClass(), e);
		}
		
	}
	
	public ListOfMessages getMessages() {
		return ListsOfMessages.getGenlabMessages();
	}
	
	public String getCurrentWorkflowFilename() {
		return currentWorkflowAbsoluteName;
	}
	
	public GenlabWorkflowInstance getCurrentWorkflowInstance() {
		return currentWorkflowInstance;
	}
	
	public void setCurrentWorkflowInstance(GenlabWorkflowInstance  w) {
		this.currentWorkflowInstance = w; 
	}
	
	private Map<String,IInputOutputInstance> id2instance = new HashMap<String, IInputOutputInstance>();
	
	public void clearCurrentAlgoInstances() {
		id2instance.clear();
	}
	
	public void addCurrentIOInstance(IInputOutputInstance io) {
		id2instance.put(io.getId(), io);
	}
	public IInputOutputInstance getCurrentIOInstance(String id) {
		return id2instance.get(id);
	}

	
	private String ensurePathCanonical(String pathRaw) {
		File pathF = new File(pathRaw);
		try {
			return pathF.getCanonicalPath();
		} catch (IOException e1) {
			throw new ProgramException("error while attempting to clean path "+pathRaw, e1);
		}
	}
	
	
	/**
	 * Saves a genlab workflow to a file.
	 * @param workflow
	 * @param file
	 */
	public void saveWorkflow(IGenlabWorkflowInstance workflow) {
		
		// ensure it was registered
		registerWorkflow(workflow);
		
		// call hooks
		GLLogger.debugTech("preparing to save workflow "+workflow+", calling hooks...", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowSaving(workflow);
		
		// save to xml
		final String filename = workflow.getAbsolutePath();
		
		// create directories if necessary
		String path = FileUtils.extractPath(filename);
		(new File(path)).mkdirs();
		
		
		GLLogger.debugTech("saving workflow as XML to "+filename, getClass());
		try {
			xstream.marshal(
					workflow, 
					new PrettyPrintWriter(new FileWriter(filename))
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the workflow "+workflow+" as XML to "+filename, getClass(), e);

		} catch (IOException e) {
			GLLogger.errorTech("error while saving the workflow "+workflow+" as XML to "+filename, getClass(), e);

		}

		filename2workflow.put(filename, workflow);
		
		GLLogger.debugTech("workflow "+workflow+" saved, calling hooks...", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowSaved(workflow);
		
		
	}
	
	public void registerWorkflow(IGenlabWorkflowInstance workflow) {
		filename2workflow.put(workflow.getAbsolutePath(), workflow);
	}
	
	public IGenlabWorkflowInstance readWorkflow(String absoluteFilename) {
		
		this.currentWorkflowAbsoluteName = absoluteFilename;
		
		IGenlabWorkflowInstance workflow = null;
		
		// first of all: maybe it already exists ?
		workflow = filename2workflow.get(absoluteFilename);		
		if (workflow != null) {
			GLLogger.debugTech("workflow "+absoluteFilename+" already loaded; ", getClass());
			return workflow;
		}
		
		File f = new File(absoluteFilename);
		GLLogger.debugTech("attempting to read a genlab workflow from: "+f.getAbsolutePath(), getClass());
		
		clearCurrentAlgoInstances();
		
		workflow = (GenlabWorkflowInstance)xstream.fromXML(f);
		
		// ... the base directory

		// now define all the transient attributes
		workflow._setFilename(absoluteFilename);
		
		registerWorkflow(workflow);
		//project.addWorkflow(workflow);
		
		clearCurrentAlgoInstances();

		return workflow;
		
	}
	
	public void addCurrentAlgoInstance(IAlgoInstance algoInstance) {
		for (IInputOutputInstance input: algoInstance.getInputInstances()) {
			addCurrentIOInstance(input);
		}
		for (IInputOutputInstance output: algoInstance.getOutputInstances()) {
			addCurrentIOInstance(output);
		}
	}
	

}
