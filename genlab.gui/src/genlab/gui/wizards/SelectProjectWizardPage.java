package genlab.gui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.misc.ContainerContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * TODO develop this common feature
 * A page wizard for selecting one project
 * 
 * @author Samuel Thiriot
 *
 */
public class SelectProjectWizardPage extends WizardPage {

	private IStructuredSelection currentSelection;
	private List widgetList = null; 
	
	public SelectProjectWizardPage(String pageName,
			IStructuredSelection selection) {
		super(pageName);
		setPageComplete(false);
		this.currentSelection = selection;
	}

	public SelectProjectWizardPage(String pageName, String title,
			ImageDescriptor titleImage,
			IStructuredSelection selection) {
		super(pageName, title, titleImage);
		setPageComplete(false);
		this.currentSelection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_BOTH));
		topLevel.setFont(parent.getFont());

		widgetList = new List(parent, SWT.SINGLE);
		
		
		// This has to be done after the viewer has been laid out
		treeViewer.setInput(ResourcesPlugin.getWorkspace());
			
		// TODO select from current selection !
	}
	
	protected boolean validatePage() {
		if (treeViewer.getSelection().isEmpty()) {
			setErrorMessage("please select a project");
			return false;
		} else {
			setErrorMessage(null);
			return true;
		}
	}

	protected void containerSelectionChanged(IContainer firstElement) {
		setPageComplete(validatePage());
			
	}

}
