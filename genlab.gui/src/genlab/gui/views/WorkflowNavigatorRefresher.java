package genlab.gui.views;

import genlab.basics.workflow.IWorkflowListener;
import genlab.basics.workflow.WorkflowHooks;
import genlab.core.algos.IGenlabWorkflow;
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
	public void workflowShown(IGenlabWorkflow workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		Utils.setCommonNavigatorInput(
				"genlab.gui.views.workflowexplorer", 
				new WorkflowRoot(workflow)
				);
	}

	@Override
	public void workflowCreation(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflow workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		Utils.updateCommonNavigator("genlab.gui.views.workflowexplorer");
	}

	@Override
	public void workflowOpened(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowSaved(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectSaved(IGenlabProject project) {
		
	}
	

}
