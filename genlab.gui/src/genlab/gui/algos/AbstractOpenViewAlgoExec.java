package genlab.gui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.AbstractAlgoExecutionOneshotOrReduce;
import genlab.core.model.exec.AbstractAlgoExecutionReduce;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ConnectionExecFromIterationToReduce;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.perspectives.OutputsGUIManagement;
import genlab.gui.views.AbstractViewOpenedByAlgo;
import genlab.quality.TestResponsivity;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * A basic class for algo executions which are supposed to open 
 * a view for a given execution. Takes care of opening the view,
 * connecting it with its execution, etc.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractOpenViewAlgoExec extends AbstractAlgoExecutionOneshotOrReduce {

	private final String SWT_THREAD_USER_ID_OPEN_VIEW = this.toString()+":open_view";

	private final String SWT_THREAD_USER_ID_DISPLAY_SYNC = this.toString()+":sync";

	private final String SWT_THREAD_USER_ID_DISPLAY_ASYNC = this.toString()+":display_async";

	private final String SWT_THREAD_USER_ID_DISPLAY_REDUCE = this.toString()+":display_reduce";

	/**
	 * if true, there is already a task submitted in the gui thread to update the display
	 */
	protected boolean updatePending = false;

	
	protected final String id;
	
	protected final String viewId;
	
	/**
	 * The view part counterpart for this algo, once the callback was called.
	 */
	protected AbstractViewOpenedByAlgo theView = null;
	
	
	
	public AbstractOpenViewAlgoExec(IExecution exec, IAlgoInstance algoInst, String viewId) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());

		this.viewId = viewId;
		
		id = // one different view for each exec
				exec.getId()+".view."+
				// and instance of console into this exec !
				System.identityHashCode(this);
		
		
	}
	
	/**
	 * Returns the id of this view, which is unique for this workflow, execution and view 
	 * @return
	 */
	public final String getId() {
		return id;		
	}
	

	
	/**
	 * Opens the view to display info
	 */
	protected void openViewSync() {
				
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					
					// id of the view (provided by the genlab.gui package)
					viewId,
					// if of the content (so several instances can be opened)
					id,
					IWorkbenchPage.VIEW_ACTIVATE
					);
			
			// transmit info to enable the view to load what is required
			WorkbenchPart v = (WorkbenchPart)view;
			
			OutputsGUIManagement.singleton.setViewExecForId(getId(), this);

			v.setPartProperty(
					OutputsGUIManagement.PROPERTY_ALGOVIEW_EXEC, 
					getId()
					);
			
		} catch (PartInitException e) {
			getResult().getMessages().warnUser("error while attempting to open the view: "+e.getLocalizedMessage(), getClass());
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			
		}
	}
	

	
	protected void openView() {
		
		Display display = Display.getDefault();
		
		if (display == null) {
			getResult().getMessages().errorTech("unable to retrieve an SWT display", getClass());
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}
		
		if (theView == null) {
			if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
				TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID_OPEN_VIEW);
			
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID_OPEN_VIEW);
					
					openViewSync();
					
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID_OPEN_VIEW);
					
				}
			});
			
			
		}
	}

	protected abstract void displayResultsSync(AbstractViewOpenedByAlgo theView);

	
	protected void displayResultsAsync(final AbstractViewOpenedByAlgo theView) {
		
		if (updatePending)
			return;
		updatePending = true;
		
		if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
			TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID_DISPLAY_ASYNC);
		
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				
				if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
					TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID_DISPLAY_ASYNC);
				
				
				// actual display
				displayResultsSync(theView);
				
				
				if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
					TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID_DISPLAY_ASYNC);

				updatePending = false;

			}
		});
	}
	
	public void callbackRegisterView(AbstractViewOpenedByAlgo theView) {
		
		GLLogger.debugTech("received a view ! Let's display results.", getClass());
		this.theView = theView;
		
		if (TestResponsivity.AUDIT_SWT_THREAD_USE) {
			TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID_DISPLAY_SYNC);
			TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID_DISPLAY_SYNC);
		}
		
		displayResultsSync(theView);
		
		if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
			TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID_DISPLAY_SYNC);
		
		
	}
	
	/**
	 * returns true if the display has to be opened
	 * @return
	 */
	protected boolean openDisplayIfNecessary() {
		
		if (theView != null)
			return false;
		
		GLLogger.debugTech("opening the  view...", getClass());
		
		openView();
		
		return true;
	}

	protected abstract void loadDataSuccessiveFromInput();
	
	@Override
	public void run() {

		try {

			getProgress().setComputationState(ComputationState.STARTED);

			setResult(new ComputationResult(getAlgoInstance(), progress, exec.getListOfMessages()));
			
			loadDataSuccessiveFromInput();

			if (!openDisplayIfNecessary())
				// if the display is already open, refresh it (else it will be refreshed at callback, once the view will be opened.
				displayResultsAsync(theView);
			
			// in fact, the actual display will (should) be done when the view if opened, and registers itself.
		
			getProgress().setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			getResult().getMessages().errorTech("error during the execution: "+e.getLocalizedMessage(), getClass(), e);
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			
		} finally {
			// now deregister this view (do enable its garbage collecting)
			
		}
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	protected abstract void displayResultsSyncReduced(AbstractViewOpenedByAlgo theView, IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value);
	
	protected void displayResultsASyncReduced(
			final AbstractViewOpenedByAlgo theView, 
			final IAlgoExecution executionRun,
			final IConnectionExecution connectionExec, 
			final Object value) {
		
		if (theView.isDisposed())
			return;
		
		if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
			TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID_DISPLAY_REDUCE);
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
					TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID_DISPLAY_REDUCE);
				
				displayResultsSyncReduced(theView, executionRun, connectionExec, value);
				
				if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
					TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID_DISPLAY_REDUCE);
				
			}
		});
	}

	@Override
	public void receiveInput(IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value) {

		// will be called in continuous mode: in this case, will display the result continuously
	
		if (connectionExec instanceof ConnectionExecFromIterationToReduce) {
			if (!openDisplayIfNecessary())
				// if the display is already open, refresh it (else it will be refreshed at callback, once the view will be opened.
				displayResultsASyncReduced(theView, executionRun, connectionExec, value);
				
		} else {
			if (!openDisplayIfNecessary())
				// if the display is already open, refresh it (else it will be refreshed at callback, once the view will be opened.
				displayResultsAsync(theView);
			
		}
		
	}

	@Override
	public void signalIncomingSupervisor(
			AbstractContainerExecutionSupervisor supervisor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signalEndOfTasksForSupervisor(
			AbstractContainerExecutionSupervisor supervisor,
			ComputationState state) {
		// TODO Auto-generated method stub
		
	}

	
	
}
