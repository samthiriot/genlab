package genlab.gui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * TODO disable linking (see parent)
 * 
 * @author Samuel Thiriot
 *
 */
public class NewWorkflowWizardFilePage extends WizardNewFileCreationPage {
	
	public NewWorkflowWizardFilePage(
			String pageName,
			IStructuredSelection selection) {
	
		super(pageName, selection);
		
		setFileExtension("glworkflow");
	}
	
	public String getRelativePath() {
		
		IPath path = getContainerFullPath();
		
		if (getFileName().endsWith(getFileExtension())) {
			path = path.append(getFileName());
		} else {
			path = path.append(getFileName()+"."+getFileExtension());
		}
		
		return path.toOSString();
	}
	
}
