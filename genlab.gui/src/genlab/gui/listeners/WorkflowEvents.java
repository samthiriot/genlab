package genlab.gui.listeners;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.LinkedList;

public class WorkflowEvents {

	public static WorkflowEvents workflowEvents = new WorkflowEvents();
	
	private Collection<IWorkflowGUIListener> listeners = new LinkedList<IWorkflowGUIListener>();
	
	public final String WORKFLOW_GUI_EVENTS_EXTENSION_ID = "genlab.gui.events.listener";

	
	public void addListener(IWorkflowGUIListener l) {
		listeners.add(l);
	}
	
	/**
	 * To be called each time a workflow is brought to front, so we can display the corresponding view
	 * @param workflow
	 */
	public void workflowShown(IGenlabWorkflow workflow) {
		
		
		for (IWorkflowGUIListener l : listeners) {
			try {
				l.workflowShown(workflow);
			} catch (Exception e) {
				GLLogger.warnTech("exception catched while calling a listener of GUI workflow", getClass(), e);
			}
		}
		
		
	}
	
	/*
	 * TODO: do we need an extension point ? 
	 *
	private void detectListeners() {

		GLLogger.debugTech("detecting GUI workflow listeners from plugins...", getClass());
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(WORKFLOW_GUI_EVENTS_EXTENSION_ID);
	    for (IConfigurationElement e : elements) {
		    Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o instanceof IWorkflowGUIListener) {
					listeners.add((IWorkflowGUIListener)o);
				} else {
					GLLogger.warnTech("detected something which is not a listener: "+o, getClass());
				}
			} catch (CoreException e1) {
				GLLogger.errorTech("error while detecting available algorithms: error with extension point "+e.getName(), getClass(), e1);
			}
			
		}
	    
		GLLogger.debugTech("detected "+listeners+" workflow GUI listeners provided by plugins", getClass());
	}
	*/
	
	private WorkflowEvents() {
		
	}

}
