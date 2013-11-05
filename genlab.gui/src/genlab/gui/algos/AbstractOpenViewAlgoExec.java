package genlab.gui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.views.AbstractViewOpenedByAlgo;

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
public abstract class AbstractOpenViewAlgoExec extends AbstractAlgoExecutionOneshot {

	/**
	 * Associates ids of these views with the corresponding instance.
	 * This enables views opened to search for their counterpart.
	 * 
	 */
	private static Map<String,AbstractOpenViewAlgoExec> id2algoView = new HashMap<String, AbstractOpenViewAlgoExec>(20);

	public static AbstractOpenViewAlgoExec getViewExecForId(String id) {
		
		GLLogger.debugTech("attempting to provide an execview for id: "+id, GraphicalConsoleExec.class);
		
		AbstractOpenViewAlgoExec res = id2algoView.get(id);
		
		return res;
	}
	
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
			id2algoView.put(getId(), this);
			v.setPartProperty(
					AbstractViewOpenedByAlgo.PROPERTY_ALGOVIEW_EXEC, 
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
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					openViewSync();
				}
			});
		}
	}
	
	protected abstract void displayResults(AbstractViewOpenedByAlgo theView);

	
	public void callbackRegisterView(AbstractViewOpenedByAlgo theView) {
		
		GLLogger.debugTech("received a console view ! Let's display results.", getClass());
		this.theView = theView;
		
		displayResults(theView);
		
	}

	@Override
	public void run() {

		try {

			setResult(new ComputationResult(getAlgoInstance(), progress, exec.getListOfMessages()));
			
			GLLogger.debugTech("opening the  view...", getClass());
			
			getProgress().setComputationState(ComputationState.STARTED);
			
			openView();
			
			// in fact, the actual display will (should) be done when the view if opened, and registers itself.
		
			getProgress().setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			getResult().getMessages().errorTech("error during the execution: "+e.getLocalizedMessage(), getClass(), e);
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			
		} finally {
			// now deregister this console (do enable its garbage collecting)
			id2algoView.remove(getId());

		}
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
