package genlab.gui.views;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.perspectives.OutputsGUIManagement;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
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
public abstract class AbstractViewOpenedByAlgo<ClassObjectToDisplay extends Object> 
									extends ViewPart 
									implements 
												IPropertyChangeListener, 
												IExecutionView, 
												IViewAttachedToAlgoInstance, 
												IPartListener2  {


	protected IExecution execution = null;
	
	protected ListOfMessages messages = ListsOfMessages.getGenlabMessages();

	protected IAlgoInstance algoInstance = null;
	
	/**
	 * true if the view knows there is more recent data but did not displayed it 
	 * because it was not displayed
	 */
	private boolean shouldBeRefreshed = false;
	
	protected ClassObjectToDisplay lastVersionDataToDisplay = null;
	
	
	public AbstractViewOpenedByAlgo() {
		
		addPartPropertyListener(this);
		
		
	}
		
	protected abstract String getName(AbstractOpenViewAlgoExec exec);


	protected abstract void dataReceived();

	/**
	 * call from algo to display data. Will display it, or not, depending on the visibility of the view
	 * @param toDisplay
	 */
	public final void receiveData(ClassObjectToDisplay toDisplay, boolean forceDisplay) {
		
		lastVersionDataToDisplay = toDisplay;
		
		if (this.getSite().getPage().isPartVisible(this)) {
			shouldBeRefreshed = false;
			dataReceived();
		} else {
			// only update visu if 
			shouldBeRefreshed = true;
			messages.traceTech("not displaying the data because the view is hidden.", getClass());
		}
		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		// at part control creation, start to listen for events related to being
		// visible or not
		getSite().getPage().addPartListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
		
		GLLogger.traceTech("received property: "+event.getProperty(), getClass());
		
		final String propertyName = event.getProperty();
		
		if (propertyName.equals(OutputsGUIManagement.PROPERTY_ALGOVIEW_EXEC)) {
			
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
		
		// unregister from workbench
		getSite().getPage().removePartListener(this);
		
		// unregister from our management
		OutputsGUIManagement.singleton.unregisterOutputGUI(this);
		
		super.dispose();
	}
	
	public abstract boolean isDisposed();

	@Override
	public final void partActivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public final void partBroughtToTop(IWorkbenchPartReference partRef) {

		// quick exit: only listen for us. 
		if (partRef.getPart(false) != this)
			return;
		
		// if the view was not refresh but should, let's do it now !
		if (shouldBeRefreshed) {
			shouldBeRefreshed = false;
			dataReceived();
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public final void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		
		// quick exit: only listen for us. 
		if (partRef.getPart(false) != this)
			return;
		
		// if the view was not refresh but should, let's do it now !
		if (shouldBeRefreshed) {
			shouldBeRefreshed = false;
			dataReceived();
		}
	}

	@Override
	public final void partInputChanged(IWorkbenchPartReference partRef) {
	}


}
