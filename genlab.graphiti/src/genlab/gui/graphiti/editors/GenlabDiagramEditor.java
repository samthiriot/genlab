package genlab.gui.graphiti.editors;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.PersistenceUtils;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.genlab2graphiti.Genlab2GraphitiUtils;
import genlab.gui.graphiti.genlab2graphiti.GenlabDomainModelChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;

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
	 * Ugly: we have to copy the content in order to retrieve the Diagram.
	 * 
	 */
	@Override
	protected void setInput(IEditorInput input) {
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
		
		IGenlabWorkflow workflow = (IGenlabWorkflow) getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(diagram);
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
