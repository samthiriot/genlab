package genlab.gui.views;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowListener;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.listeners.IWorkflowGUIListener;
import genlab.gui.listeners.WorkflowGUIEventsDispatcher;

/**
 * Listens for Workflow GUI listeners, and refreshes the views.
 */
public class WorkflowNavigatorRefresher implements IWorkflowGUIListener, IWorkflowListener {

	public WorkflowNavigatorRefresher() {
		WorkflowGUIEventsDispatcher.workflowEvents.addListener(this);
		WorkflowHooks.getWorkflowHooks().declareListener(this);
	}

	@Override
	public void workflowShown(IGenlabWorkflowInstance workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		Utils.setCommonNavigatorInput(
				"genlab.gui.views.workflowexplorer", 
				new WorkflowRoot(workflow)
				);
	}

	@Override
	public void workflowCreation(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflowInstance workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		Utils.updateCommonNavigator("genlab.gui.views.workflowexplorer");
	}

	@Override
	public void workflowOpened(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowSaved(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectSaved(IGenlabProject project) {
		
	}
	

}
