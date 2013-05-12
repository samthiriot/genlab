package genlab.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class WorkflowView extends ViewPart {

	public static final String ID = "genlab.gui.views.WorkflowView";


	public WorkflowView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
	
		Label l = new Label(parent, SWT.NONE);
		l.setText("a venir, une vue du workflow ici");
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
