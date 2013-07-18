package genlab.gui.views;

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


	public ConsoleView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		text = new StyledText(parent, SWT.BORDER);
		
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}
	

	public void initOutputSync() {
		text.setRedraw(false);
	}
	
	public void writeSync(String s) {
		text.append(s);
	}
	
	/**
	 * Note this operation is in some way costly, 
	 * because it has to shift to the SWT thread (asyncExec)
	 * @param s
	 */
	public void write(final String s) {
		text.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				writeSync(s);
			}
		});
	}
	
	public void endOutputSync() {
		text.setRedraw(true);
	}

	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}

	
}

