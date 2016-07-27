package genlab.gui.algos;

import java.util.LinkedList;
import java.util.List;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshotOrReduce;
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
 * TODO in reduce mode, this principle is loosing some displays during the time the view is opening itself...
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractOpenViewAlgoExec extends AbstractAlgoExecutionOneshotOrReduce {

	private final String SWT_THREAD_USER_ID_OPEN_VIEW = this.toString()+":open_view";

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
	
	/**
	 * If true, means the view is currently being displayed. 
	 * Avoids to recall the opening feature many times
	 */
	protected boolean viewOpeningOngoing = false;
	
	protected List<List<Object>> pendingForDisplay = new LinkedList();
	
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
		
		viewOpeningOngoing = true;
		
		Display display = Display.getDefault();
		
		if (display == null) {
			getResult().getMessages().errorTech("unable to retrieve an SWT display", getClass());
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}
		
		GLLogger.debugTech("Opening view "+getName(), getClass());
		
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

		// never refresh a disposed view
		if (theView.isDisposed())
			return;
		
		// avoid doing to successive updates
		if (updatePending)
			return;
		updatePending = true;

		try {
			displayResultsSync(theView);
		} finally {
			updatePending = false;
		}
	}
	
	public void callbackRegisterView(AbstractViewOpenedByAlgo theView) {
		
		// will be called when the view is opened from the SWT thread (async)
		// possibilities: 
		// * either we were open after starting because we are showing results as we run; in this case our state is STARTED or FINISHED (as the running block might have finished while we were opening the view) 
		// * or we were open to display results continuously; then we don't display yet all our inputs.
		
		GLLogger.debugTech("received a view ! Let's display results.", getClass());
		this.theView = theView;
		
		// if any, let's first display all the pending elements !
		synchronized (pendingForDisplay) {
			for (List<Object> elements: pendingForDisplay) {
				receiveInput(
						(IAlgoExecution)elements.get(0), 
						(IConnectionExecution)elements.get(1), 
						elements.get(2)
						);
			}
			pendingForDisplay.clear();
		}
		
		if (progress.getComputationState() == ComputationState.STARTED || progress.getComputationState() == ComputationState.FINISHED_OK)
			displayResultsSync(theView);
		
	}
	
	/**
	 * returns true if the display has to be opened
	 * @return
	 */
	protected boolean openDisplayIfNecessary() {
		
		if (theView != null)
			return false;
		if (viewOpeningOngoing)
			return true;
		
		GLLogger.debugTech("opening the  view...", getClass());
		
		openView();
		
		return true;
	}

	protected abstract void loadDataSuccessiveFromInput();
	
	@Override
	public void run() {

		System.err.println("runing view");
		
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
	
	/**
	 * Keeps aside the elements that should have been displayed, but that can't because the 
	 * view is not open yet.
	 * @param executionRun
	 * @param connectionExec
	 * @param value
	 */
	protected void keepAsideResultToDisplayLater(IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value) {
		
		synchronized (pendingForDisplay) {
			List<Object> params = new LinkedList();
			params.add(executionRun);
			params.add(connectionExec);
			params.add(value);
			pendingForDisplay.add(params);
		}
		
	}

	@Override
	public void receiveInput(IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value) {

		// will be called in continuous mode: in this case, will display the result continuously
		// if the display is not open, don't display (will be refreshed at callback)
		if (openDisplayIfNecessary()) {
			keepAsideResultToDisplayLater(executionRun, connectionExec, value);
			return;
		}
		
		if (connectionExec instanceof ConnectionExecFromIterationToReduce) {
		
			// if the display is already open, refresh it 
			displayResultsASyncReduced(theView, executionRun, connectionExec, value);
			
		} else {
			
			// we don't refresh if the view is not visible 
			// if the display is already open, refresh it (else it will be refreshed at callback, once the view will be opened.
			displayResultsAsync(theView);
			
		}
		
	}

	@Override
	public void signalIncomingSupervisor(
			AbstractContainerExecutionSupervisor supervisor) {
		// ignore
		
	}

	@Override
	public void signalEndOfTasksForSupervisor(
			AbstractContainerExecutionSupervisor supervisor,
			ComputationState state) {
		// ignore
		
	}

	
	
}
