package genlab.gui.graphiti.editors;

import java.io.File;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.PersistenceUtils;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.genlab2graphiti.Genlab2GraphitiUtils;
import genlab.gui.graphiti.genlab2graphiti.GenlabDomainModelChangeListener;
import genlab.gui.graphiti.genlab2graphiti.MappingObjects;
import genlab.gui.listeners.IWorkflowGUIListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.EditorInputAdapter;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.internal.util.ReflectionUtil;
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
public class GenlabDiagramEditor extends DiagramEditor {

	public static final String EDITOR_ID = "genlab.gui.graphiti.editors.GenlabDiagramEditor";
	
	private GenlabDomainModelChangeListener domainModelListener = null;
	private Diagram diagram = null;
	private String filename  = null;
	
	
	public String getFilename() {
		return filename;
	}
	
	public Diagram getDiagram() {
		return diagram;
	}
	public GenlabDiagramEditor() {
		GLLogger.debugTech("Diagram editor created.", getClass());
	}

	
	@Override
	protected void registerBusinessObjectsListener() {
		domainModelListener = new GenlabDomainModelChangeListener(this);
		// TODO add this as a listener of workflows
	}
	
	@Override
	protected void unregisterBusinessObjectsListener() {
		if (domainModelListener != null) {
			// TODO remove 
			domainModelListener = null;
		}
	}
	
	/**
	 * Is overridden only to retrieve the file
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		// retrieve file
		IFile file = null;
		if (input instanceof IAdaptable) {
			file = (IFile) ((IAdaptable) input).getAdapter(IFile.class);
		}
		
		if (file==null) {
			GLLogger.errorTech("unable to detect the file linked with this editor; thus the workflow will not be found !", getClass());
			return;
		}
		
		String filename = 
			file.getLocation().toOSString()
			;
		
		filename = filename.substring(
				0, 
				filename.length()-GraphitiDiagramTypeProvider.GRAPH_EXTENSION.length()-1
				);
		
		GLLogger.debugTech("found file: "+filename, getClass());
		
		// load the corresponding workflow
		

		System.err.println("hihihi");

		System.err.println(getDiagramTypeProvider().getDiagram());
		System.err.println(filename);
		
		IGenlabWorkflowInstance workflow = GenlabPersistence.getPersistence().getWorkflowForFilename(filename);
		System.err.println(workflow);

		MappingObjects.register(getDiagramTypeProvider(), workflow);
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
        monitor.subTask("saving the genlab-graphiti mapping");
        PersistenceUtils.getPersistenceUtils().persistAsXml(
				((GraphitiFeatureProvider)getDiagramTypeProvider().getFeatureProvider()).getIndependanceSolver(),
				workflow.getAbsolutePath()+Genlab2GraphitiUtils.EXTENSION_FILE_MAPPING
				);
        
        // don't only save the workflow, but also the whole project (???)
        monitor.subTask("saving Genlab project");
		GenlabPersistence.getPersistence().saveProject(workflow.getProject(), false);
        
        // for sure, we have to save the workflow
        monitor.subTask("saving Genlab workflow");
        GenlabPersistence.getPersistence().saveWorkflow(workflow);
        

	}
	
	
	
	
	
}
