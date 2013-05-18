package genlab.gui.views;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.ui.navigator.CommonNavigator;

import genlab.basics.workflow.IWorkflowListener;
import genlab.basics.workflow.WorkflowHooks;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.listeners.IWorkflowGUIListener;
import genlab.gui.listeners.WorkflowEvents;

/**
 * Listens for Workflow GUI listeners, and refreshes the views.
 */
public class WorkflowNavigatorRefresher implements IWorkflowGUIListener, IWorkflowListener {

	public WorkflowNavigatorRefresher() {
		WorkflowEvents.workflowEvents.addListener(this);
		WorkflowHooks.getWorkflowHooks().declareListener(this);
	}

	@Override
	public void workflowShown(IGenlabWorkflow workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		CommonNavigator cn = Utils.findCommonNavigator("genlab.gui.views.workflowexplorer");
		if (cn == null) {
			GLLogger.debugTech("the workflow view is closed, can't update it.", getClass());
			return;
		}
		ContentViewer cv = cn.getCommonViewer();
		cv.setInput(new WorkflowRoot(workflow));
	}

	@Override
	public void workflowCreation(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflow workflow) {
		GLLogger.debugTech("a workflow was added, attempting to open the corresponding view", getClass());
		CommonNavigator cn = Utils.findCommonNavigator("genlab.gui.views.workflowexplorer");
		if (cn == null) {
			GLLogger.debugTech("the workflow view is closed, can't update it.", getClass());
			return;
		}
		ContentViewer cv = cn.getCommonViewer();
		cv.refresh();
	}

	@Override
	public void workflowOpened(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
	}
	

}
