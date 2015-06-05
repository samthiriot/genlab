package genlab.gui.views;

import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.quality.TestResponsivity;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * TODO don't restore this view if the corresponding exec does not exists !
 * 
 * @author Samuel Thiriot
 *
 */
public class ConsoleView extends AbstractViewOpenedByAlgo {

	protected StyledText text = null;

	public static final String VIEW_ID = "genlab.gui.views.ConsoleView";

	public static final String SWT_THREAD_USER_ID = ConsoleView.class.getCanonicalName();
	
	private class ConsoleDisplayRunnable implements Runnable {
	
		public ConsoleDisplayRunnable() {

		}
		
		@Override
		public void run() {
			
			if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
				TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID);
			
			submitted = false; // allow another display !
			
			LinkedList<String> stringsToDisplayLocal = null;
			
			synchronized (locker) {
				if (stringsToDisplay.isEmpty())
					return;
				stringsToDisplayLocal = (LinkedList<String>) stringsToDisplay.clone();
				stringsToDisplay.clear();
			}
			
			text.setRedraw(false);
			for (String s: stringsToDisplayLocal) {
				text.append(s);
			}
			text.setRedraw(true);
			
			if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
				TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID);
			
		}
		
	}
	
	private boolean submitted = false;
	
	private ConsoleDisplayRunnable runnable = new ConsoleDisplayRunnable();

	private Object locker = new Object();
	private LinkedList<String> stringsToDisplay = new LinkedList<String>();
	
	public ConsoleView() {
		 
	}

	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
		
		text = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL);
		
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

	
	/**
	 * Note this operation is in some way costly, 
	 * because it has to shift to the SWT thread (asyncExec)
	 * @param s
	 */
	public void write(final String s) {
		
		// queue to the set of things to display
		synchronized (locker) {
			stringsToDisplay.add(s);
		}
		
		// when there is something to display
		if (!submitted) {	// ... if there is no refresh already queued and waiting in the SWT thread
			submitted = true;
			if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
				TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID);
			
			text.getDisplay().asyncExec(runnable);
		}
	}
	

	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}


	@Override
	public boolean isDisposed() {
		return text != null && text.isDisposed();
	}

	@Override
	protected void refreshDisplaySync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		
	}
	
	
}

