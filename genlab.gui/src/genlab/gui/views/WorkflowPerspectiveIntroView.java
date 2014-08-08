package genlab.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * The page displayed as an intro for the workflow perspective. 
 * It will remain open all the time (so that's not the true concept of intro page from eclipse).
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowPerspectiveIntroView extends AsbtractStaticMessageView {

	public static final String ID = "genlab.gui.views.WorkflowPerspectiveIntroView";

	private Label lblIntro;
	private ExpandableComposite hostPlugins;
	 
	
	public WorkflowPerspectiveIntroView() {
		super("Workflow perspective");
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		lblIntro = toolkit.createLabel(
				form.getBody(), 
				"This is the workflow perspective. It is were you can edit your workflows."
				);
		
		ExpandableComposite hostPlugins = toolkit.createExpandableComposite(
															form.getBody(), 
															ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED 
															);
		
		hostPlugins.setLayout(new RowLayout(SWT.VERTICAL));
		
	
		toolkit.createLabel(hostPlugins, "todo");
		
		hostPlugins.layout(true);
		
		
	}

	@Override
	public void dispose() {

		try {
		
			if (lblIntro != null)
				lblIntro.dispose();
			
			if (hostPlugins != null) {
				
				// TODO dispose all children
				
				hostPlugins.dispose();
			}
			
		} catch (RuntimeException e) {
			// ignore
			e.printStackTrace();
		}
		super.dispose();
	}
	
	

}
