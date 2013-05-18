package genlab.basics.workflow;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Manages the hooks (extension points) of other plugins that may react to the creation of workflows
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowHooks {

	public static final String EXTENSION_POINT_WORKFLOW_CREATION = "genlab.core.workflow.listeners";

	private static WorkflowHooks singleton = null;
	
	public static WorkflowHooks getWorkflowHooks() {
		if (singleton == null)
			singleton = new WorkflowHooks();
		return singleton;
	}
	
	private Collection<IWorkflowListener> listeners = new LinkedList<IWorkflowListener>();
	
	
	public void declareListener(IWorkflowListener l) {
		GLLogger.debugTech("detected new workflow listener: "+l, getClass());
		if (!listeners.contains(l))
			listeners.add(l);
	}
	
	public Collection<IWorkflowListener> getListeners() {
		return listeners;
	}

	private void detectFromExtensionPoints() {
		GLLogger.debugTech("detecting workflow listeners from plugins...", getClass());
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_WORKFLOW_CREATION);
	    for (IConfigurationElement e : elements) {
	    	GLLogger.debugTech("Evaluating extension: "+e.getName(), getClass());
		    Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o instanceof IWorkflowListener) {
					declareListener((IWorkflowListener) o);
				} else {
					GLLogger.warnTech("detected a something which is not a workflow listener: "+o, getClass());
				}
			} catch (CoreException e1) {
				GLLogger.errorTech("error while detecting available algorithms: error with extension point "+e.getName(), getClass(), e1);
			}
			
		}
	}
	
	private WorkflowHooks() {
		detectFromExtensionPoints();
	}

	public void notifyWorkflowCreation(IGenlabWorkflow wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowCreation(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyWorkflowChange(IGenlabWorkflow wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowChanged(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
}
