package genlab.gui.genlab2eclipse;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ExecutionHooks;
import genlab.core.model.exec.ITasksListener;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.views.MessagesView;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

public class ExecutionListener implements ITasksListener {

	public ExecutionListener() {
		GLLogger.traceTech("registering myself against the genlab central dispatcher...", getClass());
		if (ExecutionHooks.singleton == null)
			throw new RuntimeException("pfffff.... stupid !");
		ExecutionHooks.singleton.addListener(this);
	}
	
	protected void openConsole(IExecution task) {

		try {
			GLLogger.debugTech("opening a view to show messages for this execution", getClass());
			
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					
					// id of the view (provided by the genlab.gui package)
					MessagesView.ID,
					// if of the content (so several instances can be opened)
					task.getId(),
					IWorkbenchPage.VIEW_ACTIVATE
					);
			
			// transmit info to enable the view to load what is required
			WorkbenchPart v = (WorkbenchPart)view;
			v.setPartProperty(
					MessagesView.PROPERTY_MESSAGES_ID, 
					task.getId()
					);
			
		} catch (PartInitException e) {
			GLLogger.warnUser("error while attempting to open the console view: "+e.getLocalizedMessage(), getClass());
			
		}
	}

	@Override
	public void notifyParentTaskAdded(final IExecution task) {

		GLLogger.debugTech("a novel execution task started; let's open a console for it !", getClass());
		
		Display display = Display.getDefault();
		if (display == null) {
			GLLogger.warnTech("unable to find a SWT display; will not be able to open a console for this execution :-(", getClass());
			return;
		}
		
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				openConsole(task);
			}
		});
		
		
	}

}
