package genlab.gui.graphiti.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.Utils;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.genlab2graphiti.Genlab2GraphitiUtils;

public class CreateGraphitiDiagramHandler extends AbstractHandler {

	protected void createDiagramForWorkflow(IGenlabWorkflowInstance workflow) {
				
		GenLabIndependenceSolver.singleton.registerWorkflow(workflow);

		// create the empty diagram
		Genlab2GraphitiUtils.createDiagram(
				workflow, 
				genlab.gui.Utils.findEclipseProjectForWorkflow(workflow)
				);
		
		// fill the diagram
		Genlab2GraphitiUtils.fillGraphitiFromGenlab(workflow);

		// notify of the automatic creation of this diagram (shoud trigger a creation of the diagram)
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(workflow);

	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ITreeSelection selection = (ITreeSelection)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
				
		for (TreePath path: selection.getPaths()) {
		
			GLLogger.debugUser("should create a graph for file "+path.getLastSegment(), getClass());
			
			// retrieve the workflow for the current segment
			IFile file = (IFile)path.getLastSegment();
			
			IGenlabWorkflowInstance workflow = GenlabPersistence.getPersistence().readWorkflow(file.getLocation().toOSString());
			
			createDiagramForWorkflow(workflow);
			
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isEnabled() {
		return true;
	}

	@Override
	public final boolean isHandled() {
		return true;
	}


}
