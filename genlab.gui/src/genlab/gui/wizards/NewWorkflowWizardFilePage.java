package genlab.gui.wizards;

import genlab.core.persistence.GenlabPersistence;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
		
		setFileExtension(GenlabPersistence.EXTENSION_WORKFLOW.substring(1));
	}
	
	public String getRelativePath() {
		
		IPath path = getContainerFullPath();
		
		if (getFileName().endsWith(getFileExtension())) {
			path = path.append(getFileName());
		} else {
			path = path.append(getFileName()+getFileExtension());
		}
		
		return path.toOSString();
	}
	
	public IProject getSelectedProject() {

		try {
			String projectName = getContainerFullPath().uptoSegment(1).toString();
			return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		} catch (Throwable e){
			return null;
		}
	}
	
}
