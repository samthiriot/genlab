package genlab.core.persistence;

import genlab.core.commons.FileUtils;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.projects.GenlabProject;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class GenlabPersistence {

	public static final String EXTENSION_PROJECT = ".glp"; // GenLabProject
	public static final String EXTENSION_WORKFLOW = ".glw"; // GenLabWorkflow
	
	public static final String FILENAME_PROJECT = "project"+EXTENSION_PROJECT;

	public static final String XMLTAG_ID = "glid"; 

	public static final String XMLTAG_PROJECT = "project"; 
	public static final String XMLTAG_WORKFLOW = "workflow"; 
	public static final String XMLTAG_ALGOINSTANCE = "algo"; 
	public static final String XMLTAG_CONNECTIONINSTANCE = "connection"; 

	private static GenlabPersistence singleton = new GenlabPersistence();

	
	public static GenlabPersistence getPersistence() {
		return singleton;
	}
	
	private static Map<String, IGenlabWorkflowInstance> filename2workflow = new HashMap<String, IGenlabWorkflowInstance>();

	private XStream xstream; 

	/**
	 * Stores the project currently saved or restored.
	 */
	private IGenlabProject currentProject = null;
	
	private String currentWorkflowRelativeName = null;

	private GenlabWorkflowInstance currentWorkflowInstance = null;

	
	public IGenlabWorkflowInstance getWorkflowForFilename(String filename) {
		return filename2workflow.get(filename);
	}
	
	private GenlabPersistence() {
		
		// init xtream
		GLLogger.debugTech("initializing xstream for persistence...", getClass());
		try {
			xstream = new XStream(new StaxDriver());
	
			xstream.alias(XMLTAG_PROJECT, GenlabProject.class);
			
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
	
	
	public void saveProject(IGenlabProject project) {
		this.saveProject(project, true);
	}
	
	public IGenlabProject getCurrentProject() {
		return currentProject;
	}
	
	public String getCurrentWorkflowFilename() {
		return currentWorkflowRelativeName;
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

	
	public void saveProject(IGenlabProject project, boolean saveWorkflows) {
		
		GLLogger.debugTech("preparing to save project "+project+"...", getClass());
		
		this.currentProject = project;
		
		// call hooks for each workflow
		
		//	TODO hooks for projects ?!
		final String filename = project.getProjectSavingFilename();
		
		// create directories if necessary
		String path = FileUtils.extractPath(filename);
		(new File(path)).mkdirs();
		
		// save project
		GLLogger.debugTech("saving project as XML to "+filename, getClass());
		try {
			xstream.toXML(
					project,
					new PrintStream(filename)
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the workflow "+project+" as XML to "+filename, getClass(), e);

		}
		
		this.currentProject = null;
		
		// hooks
		WorkflowHooks.getWorkflowHooks().notifyProjectSaved(project);
		
		if (saveWorkflows) {
			// save workflows...
			for (IGenlabWorkflowInstance workflow : project.getWorkflows()) {
				GLLogger.debugTech("saving workflow: "+workflow, getClass());
				try {
					GenlabPersistence.getPersistence().saveWorkflow(workflow);
				} catch (Exception e) {
					GLLogger.errorTech("error while saving workflow "+workflow, getClass(), e);
					// TODO warn user
				}
			}
		}
	}
	
	public IGenlabProject readProject(String baseDirectory) {
		
		
		File f = new File(baseDirectory+File.separator+FILENAME_PROJECT);
		GLLogger.debugTech("attempting to read a genlab project from: "+f.getAbsolutePath(), getClass());
		
		GenlabProject project = (GenlabProject)xstream.fromXML(f);
		
		this.currentProject = project;
		
		// readen, now complete the fields for transient files
		
		// ... the base directory
		project._setBaseDirectory(baseDirectory);
		
		// ... the sub workflows
		for (String relativeWorkflowFilename : project.getWorkflowPathes()) {
			this.currentWorkflowRelativeName = relativeWorkflowFilename;
			readWorkflow(project, relativeWorkflowFilename);		
		}
		this.currentWorkflowRelativeName = null;
		
		this.currentProject = null;
		
		
		return project;
	}
	
	/**
	 * Saves a genlab workflow to a file.
	 * @param workflow
	 * @param file
	 */
	public void saveWorkflow(IGenlabWorkflowInstance workflow) {
		
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

		filename2workflow.put(filename, workflow);
		
		GLLogger.debugTech("workflow "+workflow+" saved, calling hooks...", getClass());
		WorkflowHooks.getWorkflowHooks().notifyWorkflowSaved(workflow);
		
		
	}
	
	public IGenlabWorkflowInstance readWorkflow(IGenlabProject project, String relativeFilename) {
		
		File f = new File(project.getBaseDirectory()+File.separator+relativeFilename);
		GLLogger.debugTech("attempting to read a genlab workflow from: "+f.getAbsolutePath(), getClass());
		
		GenlabWorkflowInstance workflow = (GenlabWorkflowInstance)xstream.fromXML(f);
		
		// now define all the transient attributes
		workflow._setProject(project);
		workflow._setFilename(relativeFilename);
		
		filename2workflow.put(f.getAbsolutePath(), workflow);
		
		return workflow;
		
	}
	
	public void persistAsXml(Object myObject, String absoluteFilename) {
		
		GLLogger.debugTech("saving an object "+myObject.getClass().getCanonicalName()+" as XML to "+absoluteFilename, getClass());
		try {
			xstream.toXML(
					myObject,
					new PrintStream(absoluteFilename)
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the object "+myObject.getClass().getCanonicalName()+" as XML to "+absoluteFilename, getClass(), e);

		}
	}
	
	public Object loadAsXml(String absoluteFilename) {
		
		GLLogger.debugTech("reading an object from XML: "+absoluteFilename, getClass());
		try {
			Object myObject = xstream.fromXML(new File(absoluteFilename));
			return myObject;
		} catch (com.thoughtworks.xstream.io.StreamException e) {
			GLLogger.warnTech("was unable to load a persisted element from xml: "+absoluteFilename, getClass(), e);
			return null;
		}
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
