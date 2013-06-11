package genlab.gui.listeners;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;

/**
 * Somesthing that listens to GUI events related to workflows, like the workflow is shown, etc.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IWorkflowGUIListener {

	public void workflowShown(IGenlabWorkflowInstance workflow);
	
}
