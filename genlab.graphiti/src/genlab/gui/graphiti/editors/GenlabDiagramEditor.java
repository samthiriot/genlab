package genlab.gui.graphiti.editors;

import java.io.File;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowContentListener;
import genlab.core.model.instance.IWorkflowListener;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.genlab2graphiti.Genlab2GraphitiUtils;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;

/**
 * Specific diagram editor that does not sync with EMF objects
 * 
 * TODO listen for workflow changes
 * 
 * @author Samuel Thiriot
 */
public class GenlabDiagramEditor extends DiagramEditor implements IWorkflowEditor, IWorkflowListener {

	public static final String EDITOR_ID = "genlab.gui.graphiti.editors.GenlabDiagramEditor";
	
	private Diagram diagram = null;
	private String filename  = null;
	
	private IGenlabWorkflowInstance workflow = null;
	
	private boolean isWorkflowChanged = false;
	
	public String getFilename() {
		return filename;
	}
	
	public Diagram getDiagram() {
		return diagram;
	}
	public GenlabDiagramEditor() {
		GLLogger.debugTech("Diagram editor created.", getClass());
	}

	
	/**
	 * Is overridden only to retrieve the file
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		GLLogger.debugTech("a graphiti editor was opened, attempting to link it with genlab resources...", getClass());
		
		// retrieve file
		
		IFile file = null;
		if (input instanceof IAdaptable) {
			file = (IFile) ((IAdaptable) input).getAdapter(IFile.class);
		}
		if (file==null) {
			GLLogger.errorTech("unable to detect the file linked with this editor; thus the workflow will not be found !", getClass());
			return;
		}

		String filename = file.getLocation().toOSString();
		
		// TODO be sure it is a workflow
		// this is not a graph; this is a workflow
		// let's create one first. This should open a diagram editor
		if (!file.getFileExtension().equalsIgnoreCase(GraphitiDiagramTypeProvider.GRAPH_EXTENSION)) {

			// load the corresponding workflow
			workflow = GenlabPersistence.getPersistence().getWorkflowForFilename(filename);

			// register the workflow so we can map its keys and so on
			GenLabIndependenceSolver.singleton.registerWorkflow(workflow);

			// register the workflow so we can map its keys and so on
			// this call is a bit redondant but solves problems of race conditions with the GUI 
			//GenLabIndependenceSolver.singleton.registerWorkflow(workflow);

			Genlab2GraphitiUtils.createDiagram(
					workflow, 
					Utils.findEclipseProjectForWorkflow(workflow)
					);
			
			workflow.addListener(WorkflowListener.lastInstance);
			return;
		} 
		
	
		// we are reading a graph extension

		// search for the corresponding workflow, which should be 
		filename = filename.substring(
				0, 
				filename.length()-GraphitiDiagramTypeProvider.GRAPH_EXTENSION.length()-1
				);

		{
			File fTest = new File(filename);
			if (fTest.exists() && fTest.isFile() && fTest.canRead()) {
				GLLogger.debugTech("found file for the workflow: "+filename, getClass());		
			} else {
				GLLogger.errorTech("this file does not exists; so this editor will not work properly: "+filename, getClass());
			}
		}
		

		// load the corresponding workflow
		workflow = GenlabPersistence.getPersistence().getWorkflowForFilename(filename);

		// register the workflow so we can map its keys and so on
		GenLabIndependenceSolver.singleton.registerWorkflow(workflow);

		// OR, just start to match objects ???
		GraphitiFeatureProvider dfp = (GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider();

		Genlab2GraphitiUtils.linkInTransaction(dfp, getDiagramTypeProvider().getDiagram(), workflow);
		
		
		((GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()).associateWorkflowWithThisProvider(workflow);
		
		//workflow.addListener(this);
		
		if (diagram == null) {
			diagram = (Diagram) dfp.getPictogramElementForBusinessObject(workflow);
		}

		if (diagram == null) {
			GLLogger.errorTech("Unable to find the diagram; will not be created.", getClass());
			return;
		}
		
		isWorkflowChanged = false;
		
		// TODO check consistency ???
		Genlab2GraphitiUtils.fillGraphitiFromGenlab(
				workflow, 
				diagram, 
				(GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()
				);
		
        // listen to the workflow lifecycle, so when the workflow is saved, we set our state to clean again
		workflow.addListener(WorkflowListener.lastInstance);
        WorkflowHooks.getWorkflowHooks().declareListener(this);

	}
	
	/**
	 * Supposed to received a .glw workflow file.
	 * Will first ask genlab to open this workflow.
	 * Then will ask genlab to open its own diagram.
	 * 
	 */
	@Override
	protected void setInput(IEditorInput input) {
		
		// Check the input
		/*
		if (input == null) {
			throw new IllegalArgumentException("The IEditorInput must not be null"); //$NON-NLS-1$
		}
		if (!(input instanceof IDiagramEditorInput)) {
			throw new IllegalArgumentException("The IEditorInput has the wrong type: " + input.getClass()); //$NON-NLS-1$
		}
		
		IDiagramEditorInput editorInput = (IDiagramEditorInput)input;
		String path = editorInput.getUri().toFileString();
		if (!path.endsWith("."+GraphitiDiagramTypeProvider.GRAPH_EXTENSION)) {
			throw new IllegalArgumentException("Unable to find the corresponding workflow file");
		}
		
		System.err.println("!!!!!!\nshould open file: "+path.substring(0, path.length()-GraphitiDiagramTypeProvider.GRAPH_EXTENSION.length()-1));
		*/
		// 
		super.setInput(input);
		
	}



	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		
		// retrieve required data
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		
		IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance) getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(diagram);
		if (workflow == null) {
        	GLLogger.warnTech("will not save workflow: unable to find the workflow ", getClass());
        	return;
        }
		
        // for sure, we have to save the workflow
        monitor.subTask("saving Genlab workflow");
        GenlabPersistence.getPersistence().saveWorkflow(workflow);
        
        // our dirty change will change indirectly when, and if, the corresponding genlab event comes back.
        
	}

	@Override
	public IGenlabWorkflowInstance getEditedWorkflow() {
		return workflow;
	}

	@Override
	public boolean isDirty() {

		return isWorkflowChanged;
	}


	@Override
	public void close() {

		
		super.close();
		
	}
	

	protected void setClean() {
		
		// change our internal state
		this.isWorkflowChanged = false;
		
		// and ask Eclipse to refresh our dirty status
		firePropertyChange(PROP_DIRTY);
		
	}
	
	protected void setDirty() {
		
		// change our internal state
		this.isWorkflowChanged = true;
		
		// and ask Eclipse to refresh our dirty status
		firePropertyChange(PROP_DIRTY);
		
	}


	@Override
	public void workflowCreation(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflowInstance workflow) {
		if (this.workflow == workflow)
			setDirty();
	}

	@Override
	public void workflowOpened(IGenlabWorkflowInstance workflow) {
	}

	@Override
	public void workflowSaving(IGenlabWorkflowInstance workflow) {
	}

	@Override
	public void workflowSaved(IGenlabWorkflowInstance workflow) {
		if (this.workflow == workflow)
			setClean();
	}

	@Override
	public void workflowAutomaticallyCreatedAndFinished(IGenlabWorkflowInstance instance) {
	}

	@Override
	public void workflowLoaded(IGenlabWorkflowInstance instance) {		
	}
	
	
	
}
