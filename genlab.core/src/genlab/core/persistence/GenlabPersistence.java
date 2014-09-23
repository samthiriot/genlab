package genlab.core.persistence;

import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.RunElement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class GenlabPersistence extends AbstractPersistence {

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
	private static Map<String,GenlabProject> filename2project = new HashMap<String, GenlabProject>();

	/**
	 * Stores the project currently saved or restored.
	 */
	private IGenlabProject currentProject = null;
	
	private String currentWorkflowRelativeName = null;

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
	
	/**
	 * searches, for a given file, which project it belongs to, 
	 * by searching a project as a parent of this. Returns 
	 * null if none found.
	 * @param f
	 * @return
	 */
	public IGenlabProject searchProjectForFile(File f) {
		
		// search for the file
		File parentFile = f;
		File testedFile = null;
		
		while (parentFile != null) {
			// search the 
			testedFile = new File(parentFile, FILENAME_PROJECT);
			GLLogger.traceTech("attempting to load a project file from file: "+testedFile, getClass());

			if (testedFile.exists()) {
				GLLogger.traceTech("found a project file for this file: "+parentFile, getClass());
				break;
			}
			parentFile = parentFile.getParentFile();
		}
		
		// search the corresponding project
		IGenlabProject project;
		try {
			project = filename2project.get(testedFile.getCanonicalPath());
		} catch (IOException e) {
			throw new ProgramException("error with canonical path", e);
		}
		if (project == null) {
			GLLogger.debugTech("this project was not yet loaded, will load it "+parentFile, getClass());
			project = readProject(parentFile.getAbsolutePath());
		}

		if (project == null) {
			GLLogger.warnTech("was unable to find or load a project for "+parentFile, getClass());
		}
		
		return project;
		
	}
	
	public IGenlabProject searchProjectForFile(String filename) {
		return searchProjectForFile(new File(filename));
	}
	
	private GenlabPersistence() {
		
		super();
		
		// init xtream
		GLLogger.debugTech("initializing xstream for persistence...", getClass());
		try {
	
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
			/*
			xstream.toXML(
					project,
					new PrintStream(filename)
					);
			*/
			xstream.marshal(
					project, 
					new PrettyPrintWriter(new FileWriter(filename))
					);

		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the workflow "+project+" as XML to "+filename, getClass(), e);

		} catch (IOException e) {
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
	
	private String ensurePathCanonical(String pathRaw) {
		File pathF = new File(pathRaw);
		try {
			return pathF.getCanonicalPath();
		} catch (IOException e1) {
			throw new ProgramException("error while attempting to clean path "+pathRaw, e1);
		}
	}
	
	public IGenlabProject readProject(String baseDirectoryRaw) {
	
		String baseDirectory = ensurePathCanonical(baseDirectoryRaw);
				
		// first attempt to find it already loaded
		GenlabProject project = filename2project.get(baseDirectory);
		if (project != null) {
			GLLogger.debugTech("project already loaded,  return existing state", getClass());
			return project;
		}
		
		project = GenlabProject.getProject(baseDirectory);
		if (project != null) {
			GLLogger.debugTech("project already loaded,  return existing state", getClass());
			return project;
		}
		
		final File f = new File(baseDirectory+File.separator+FILENAME_PROJECT);
		GLLogger.debugTech("attempting to read a genlab project from: "+f.getAbsolutePath(), getClass());
		
		project = (GenlabProject)xstream.fromXML(f);
		
		this.currentProject = project;
		
		// readen, now complete the fields for transient files
		
		// ... the base directory
		project._setBaseDirectory(baseDirectory);
    	GenlabProject.registerOpenedProject(project);

		filename2project.put(f.getAbsolutePath(), project);
		

		// ... the sub workflows
    	if (autoloadAllWorkflows) { 
    		LinkedList<String> toRemove = null;
			for (String relativeWorkflowFilename : project.getWorkflowPathes()) {
				try {
					this.currentWorkflowRelativeName = relativeWorkflowFilename;
					IGenlabWorkflowInstance workflow = readWorkflow(project, relativeWorkflowFilename);
					project.addWorkflow(workflow);
				} catch (RuntimeException e) {
					GLLogger.errorUser("a workflow file was not found: "+relativeWorkflowFilename+"; we will remove the reference of to this file in the projet.", getClass());
					if (toRemove == null) {
						toRemove = new LinkedList<String>();
					}
					toRemove.add(relativeWorkflowFilename);
				}
				
			} 
			if (toRemove != null)
				for (String relativeFilename: toRemove) {
					project.removeWorkflow(relativeFilename);
				}
			
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
	
	public IGenlabWorkflowInstance readWorkflow(IGenlabProject project, String relativeFilename) {
		
		this.currentWorkflowRelativeName = relativeFilename;
		
		GenlabWorkflowInstance workflow = null;
		
		// first of all: maybe it already exists ?
		workflow = (GenlabWorkflowInstance) project.getWorkflowForFilename(relativeFilename);
		
		if (workflow != null) {
			GLLogger.debugTech("workflow "+relativeFilename+" already loaded; ", getClass());
			return workflow;
		}
		
		
		File f = new File(project.getBaseDirectory()+File.separator+relativeFilename);
		GLLogger.debugTech("attempting to read a genlab workflow from: "+f.getAbsolutePath(), getClass());
		
		this.currentProject = project;
		
		clearCurrentAlgoInstances();
		
		workflow = (GenlabWorkflowInstance)xstream.fromXML(f);
		
		// ... the base directory

		// now define all the transient attributes
		workflow._setProject(project);
		workflow._setFilename(relativeFilename);
		
		registerWorkflow(workflow);
		//project.addWorkflow(workflow);
		
		this.currentProject = null;
		
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
