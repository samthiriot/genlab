package genlab.gui.views;

import org.eclipse.swt.widgets.Composite;

public class RuntimePerspectiveIntroView extends AsbtractStaticMessageView {

	public static final String ID = "genlab.gui.views.RuntimePerspectiveIntroView";

	public RuntimePerspectiveIntroView() {
		super("Runtime perspective");
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		toolkit.createLabel(
				form.getBody(), 
				"This is the runtime perspective. It is were you can monitor the progress of computations and where the displays will be opened (if you added any display algorithm to your workflow)."
				);
		
	}
	
	

}
