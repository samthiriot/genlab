package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.editors.GenlabDiagramEditor;
import genlab.gui.listeners.WorkflowGUIEventsDispatcher;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class Genlab2GraphitiUtils {

	public static final String KEY_WORKFLOW_TO_GRAPHITI_FILE = "graphiti_file";
	public static final String EXTENSION_FILE_MAPPING = ".mapping";

// TODO remove 	public static final String KEY_WORKFLOW_TO_INDEPENDANCE_SOLVER = "graphiti_independance_solver";

	
	// TODO remove old code for errors
	public static void createDiagram(IGenlabWorkflow workflow, IProject project) {
		
		GLLogger.debugTech("creating a diagram for this workflow", Genlab2GraphitiUtils.class);
		
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
		
		// associate diagram with the workflow
		// TODO associate something ?
		// MappingObjects.register(operation.getDiagram(), workflow);
		// also associate the diagram file with the workflow
		//MappingObjects.register(workflow.getAbsolutePath(), workflow);
		//MappingObjects.register(workflow.getAbsolutePath()+"."+GraphitiDiagramTypeProvider.GRAPH_EXTENSION, workflow);
		
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
			WorkflowGUIEventsDispatcher.workflowEvents.workflowShown(workflow);
		} catch (PartInitException e) {
			IStatus status = new Status(IStatus.ERROR, "org.eclipse.graphiti.examples.tutorial", e.getMessage(), e); //$NON-NLS-1$
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "oops", e.getMessage(), status);
		}
		
		
		
	}

}
