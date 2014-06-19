package genlab.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

/**
 * This view just displays a form.
 * 
 * @author B12772
 */
public class AsbtractStaticMessageView extends ViewPart {

	protected FormToolkit toolkit = null;
	protected ScrolledForm form = null;
	protected String title;
	
	
	public AsbtractStaticMessageView(String title) {
		this.title = title;
	}
	
	protected void setTitle(String title) {
		
		this.title = title;
		if (form != null) {
			form.setText(title);
		}
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		form = toolkit.createScrolledForm(parent);
		Layout layout = new RowLayout(SWT.VERTICAL);
		form.getBody().setLayout(layout);

		form.setText(title);
		
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	@Override
	public void dispose() {
		form.dispose();
		toolkit.dispose();
		super.dispose();
	}
	
	

}
