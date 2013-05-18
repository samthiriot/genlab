package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.editors.GenlabDiagramEditor;
import genlab.gui.listeners.WorkflowEvents;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class Genlab2GraphitiUtils {

	public static final String KEY_WORKFLOW_TO_GRAPHITI_FILE = "graphiti_file";
	
	
	public static void createDiagram(IGenlabWorkflow workflow, IProject project) {
		
		GLLogger.debugTech("creating a diagram for this workflow", Genlab2GraphitiUtils.class);
		
		// retrieve services
		IPeService peService = Graphiti.getPeService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();
		
		// retrieve resources
		final ResourceSetImpl resourceSet = new ResourceSetImpl();
	        
        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        
        
        // Create the data within a command and save (must not happen inside
		// the command since finishing the command will trigger setting the 
		// modification flag on the resource which will be used by the save
		// operation to determine which resources need to be saved)
		AddAllClassesCommand operation = new AddAllClassesCommand(project, editingDomain, workflow);
		editingDomain.getCommandStack().execute(operation);
		try {
			operation.getCreatedResource().save(null);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, "org.eclipse.graphiti.examples.tutorial", e.getMessage(), e); //$NON-NLS-1$
			e.printStackTrace();
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "oops", e.getMessage(), status);
			
		}
		
		MappingObjects.register(operation.getDiagram().eResource().getURI(), workflow);
		
		// Dispose the editing domain to eliminate memory leak
		editingDomain.dispose();
		
		// Open the editor
		String platformString = operation.getCreatedResource().getURI().toPlatformString(true);
		IFile file = project.getParent().getFile(new Path(platformString));
		IFileEditorInput input = new FileEditorInput(file);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					input, 
					GenlabDiagramEditor.EDITOR_ID
					);
			WorkflowEvents.workflowEvents.workflowShown(workflow);
		} catch (PartInitException e) {
			IStatus status = new Status(IStatus.ERROR, "org.eclipse.graphiti.examples.tutorial", e.getMessage(), e); //$NON-NLS-1$
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "oops", e.getMessage(), status);
		}
		
		
		
	}

}
