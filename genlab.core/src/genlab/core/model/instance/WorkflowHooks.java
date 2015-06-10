package genlab.core.model.instance;

import genlab.core.projects.IGenlabProject;
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
	
	public void removeListener(IWorkflowListener l) {
		listeners.remove(l);
	}
	public Collection<IWorkflowListener> getListeners() {
		return listeners;
	}

	/**
	 * Detects listener registered using extension points.
	 */
	private void detectFromExtensionPoints() {
		
		GLLogger.debugTech("detecting workflow listeners from plugins...", getClass());
	    
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		if (reg == null) {
			GLLogger.warnTech("unable to find extension registry; no workflow listener can be detected from plugins.", getClass());
			return;
		}
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

	public void notifyWorkflowCreation(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowCreation(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyWorkflowChange(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowChanged(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyWorkflowSaving(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowSaving(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyWorkflowSaved(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowSaved(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyWorkflowAutomaticallyDone(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowAutomaticallyCreatedAndFinished(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	

	public void notifyWorkflowLoaded(IGenlabWorkflowInstance wf) {
		for (IWorkflowListener l : listeners) {
			try {
				l.workflowLoaded(wf);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
	
	public void notifyProjectSaved(IGenlabProject project) {
		for (IWorkflowListener l : listeners) {
			try {
				l.projectSaved(project);
			} catch (RuntimeException e) {
				GLLogger.warnTech("in the workflow listener, catched an exception: "+e.getMessage(), getClass(), e);
			}
		}
	}
}
