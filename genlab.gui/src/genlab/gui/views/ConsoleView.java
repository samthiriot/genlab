package genlab.gui.views;

import java.util.LinkedList;

import genlab.gui.algos.AbstractOpenViewAlgoExec;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO don't restore this view if the corresponding exec does not exists !
 * 
 * @author Samuel Thiriot
 *
 */
public class ConsoleView extends AbstractViewOpenedByAlgo {

	protected StyledText text = null;

	public static final String VIEW_ID = "genlab.gui.views.ConsoleView";

	
	private class ConsoleDisplayRunnable implements Runnable {
	
		public ConsoleDisplayRunnable() {

		}
		
		@Override
		public void run() {
			
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
			text.getDisplay().asyncExec(runnable);
		}
	}
	

	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}

	
	
}

