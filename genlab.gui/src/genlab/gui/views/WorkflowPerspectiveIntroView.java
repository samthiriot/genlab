package genlab.gui.views;

import org.eclipse.swt.widgets.Composite;

public class WorkflowPerspectiveIntroView extends AsbtractStaticMessageView {

	public static final String ID = "genlab.gui.views.WorkflowPerspectiveIntroView";

	public WorkflowPerspectiveIntroView() {
		super("Workflow perspective");
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		toolkit.createLabel(
				form.getBody(), 
				"This is the workflow perspective. It is were you can edit your workflows."
				);
		
	}
	
	

}
