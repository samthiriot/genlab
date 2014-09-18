package genlab.gui.graphiti.editors;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.genlab2graphiti.Genlab2GraphitiUtils;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Specific diagram editor that does not sync with EMF objects
 * 
 * TODO listen for workflow changes
 * 
 * @author Samuel Thiriot
 */
public class GenlabDiagramEditor extends DiagramEditor implements IWorkflowEditor {

	public static final String EDITOR_ID = "genlab.gui.graphiti.editors.GenlabDiagramEditor";
	
	private Diagram diagram = null;
	private String filename  = null;
	
	private IGenlabWorkflowInstance workflow = null;
	
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
		IGenlabProject project = GenlabPersistence.getPersistence().searchProjectForFile(filename);
		System.err.println("project : "+project);
		
		workflow = GenlabPersistence.getPersistence().getWorkflowForFilename(filename);
		System.err.println(workflow);

		// OR, just start to match objects ???
		GraphitiFeatureProvider dfp = (GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider();

		Genlab2GraphitiUtils.linkInTransaction(dfp, getDiagramTypeProvider().getDiagram(), workflow);
		
		// TODO remove ???
		GenLabIndependenceSolver.singleton.registerWorkflow(workflow);
		
		((GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()).associateWorkflowWithThisProvider(workflow);
		
		workflow.addListener(WorkflowListener.lastInstance);
		
		if (diagram == null) {
			diagram = (Diagram) dfp.getPictogramElementForBusinessObject(workflow);
		}

		if (diagram == null) {
			
			GLLogger.errorTech("Unable to find the diagram; will not be created.", getClass());
			return;
		}
		
		
		// TODO check consistency ???
		Genlab2GraphitiUtils.fillGraphitiFromGenlab(
				workflow, 
				diagram, 
				(GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()
				);
		
	}
	
	/**
	 * Supposed to received a .glworkflow file.
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
		
		
		// ugly: retrieved from DefaultPersistence. THe idea is to retrieve the diagram somewhere (and this is a good place, for sure)
		/*
		EObject modelElement = null;
		try {
			modelElement = getEditingDomain().getResourceSet().getEObject(input.get, false);
			if (modelElement == null) {
				modelElement = getEditingDomain().getResourceSet().getEObject(uri, true);
				if (modelElement == null) {
					return;
				}
			}
			this.diagram = (Diagram) modelElement;
		} catch (WrappedException e) {
			GLLogger.warnTech("unable to retrieve diagram", getClass());
			return;
		}
		*/
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
		
		// save the mapping between genlab and graphiti
		/*
        monitor.subTask("saving the genlab-graphiti mapping");
        PersistenceUtils.getPersistenceUtils().persistAsXml(
				((GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()).getIndependanceSolver(),
				workflow.getAbsolutePath()+Genlab2GraphitiUtils.EXTENSION_FILE_MAPPING
				);
        */
		
        // don't only save the workflow, but also the whole project (???)
        monitor.subTask("saving Genlab project");
		GenlabPersistence.getPersistence().saveProject(workflow.getProject(), false);
        
        // for sure, we have to save the workflow
        monitor.subTask("saving Genlab workflow");
        GenlabPersistence.getPersistence().saveWorkflow(workflow);
        

	}

	@Override
	public IGenlabWorkflowInstance getEditedWorkflow() {
		return workflow;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return super.isDirty();
	}
	
	
	
	
	
}
