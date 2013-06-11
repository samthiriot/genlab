package genlab.gui.views;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WorkflowNavigatorContentProvider  implements ITreeContentProvider {

	public WorkflowNavigatorContentProvider() {
		GLLogger.debugTech("workflow navigator created / content provider", getClass());
		new WorkflowNavigatorRefresher();
			
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// not possible ?! 
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof WorkflowRoot) {
			return new Object[]{((WorkflowRoot)inputElement).workflow};
		} else if (inputElement instanceof IGenlabWorkflowInstance) {
			IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance)inputElement;
			return workflow.getAlgoInstances().toArray();
		}
		GLLogger.warnTech("can't provide children for this parent "+inputElement, getClass());
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IGenlabWorkflowInstance) {
			IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance)parentElement;
			return workflow.getAlgoInstances().toArray();
		}
		GLLogger.warnTech("can't provide children for this parent "+parentElement, getClass());
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IAlgoInstance) {
			return ((IAlgoInstance)element).getWorkflow();
		} else if (element instanceof IGenlabWorkflow) {
			return null;
		}
		GLLogger.warnTech("can't provide parent for "+element, getClass());

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IAlgoInstance) {
			return false;
		} else if (element instanceof IGenlabWorkflowInstance) {
			return !((IGenlabWorkflowInstance)element).getAlgoInstances().isEmpty();
		}
		return false;
	}

}
