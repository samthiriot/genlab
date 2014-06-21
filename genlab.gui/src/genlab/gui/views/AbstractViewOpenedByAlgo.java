package genlab.gui.views;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.perspectives.OutputsGUIManagement;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.part.ViewPart;

/**
 * Basic class for a view which is opened by the execution of an algorithm.
 * 
 * A good practice, for each plugin which propose the opening of views, is to provide also a Helper class
 * which opens easily windows, without an explicit call the eclipse RCP methods.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractViewOpenedByAlgo extends ViewPart implements IPropertyChangeListener, IExecutionView, IViewAttachedToAlgoInstance  {

	
	protected IExecution execution = null;
	
	protected ListOfMessages messages = ListsOfMessages.getGenlabMessages();

	protected IAlgoInstance algoInstance = null;
	
	public AbstractViewOpenedByAlgo() {
		addPartPropertyListener(this);
	}
		
	protected abstract String getName(AbstractOpenViewAlgoExec exec);

	/**
	 * called when the data was received (the execution, messages and algoInstance members are set)
	 */
	protected void dataReceived() {
		
	}

	@Override
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
		
		GLLogger.traceTech("received property: "+event.getProperty(), getClass());
		
		if (event.getProperty().equals(OutputsGUIManagement.PROPERTY_ALGOVIEW_EXEC)) {
			
			AbstractOpenViewAlgoExec exec = OutputsGUIManagement.singleton.getViewExecForId((String) event.getNewValue());
			if (exec == null) {
				GLLogger.warnTech("unable to retrieve the content to display for exec: "+event.getNewValue(), getClass());
				return;
			}
			
			// call the callback of the original caller (the algo asked for the creation of this view, it is now created, it is informed and given a opportunity to act ^^)
			exec.callbackRegisterView(this);
			
			setPartName(getName(exec));
			
			setExecution(exec);
			
		}
	}
	
	@Override
	public IExecution getExecution() {
		return execution;
	}

	public void setExecution(AbstractOpenViewAlgoExec exec) {
		this.execution = exec.getExecution();
		this.messages = exec.getExecution().getListOfMessages();
		this.algoInstance = exec.getAlgoInstance();
		OutputsGUIManagement.singleton.registerOutputGUI(this);

	}
	@Override
	public IAlgoInstance getAlgoInstance() {
		return algoInstance;
	}

	@Override
	public void dispose() {
		OutputsGUIManagement.singleton.unregisterOutputGUI(this);
		super.dispose();
	}
	
	public abstract boolean isDisposed();

}
