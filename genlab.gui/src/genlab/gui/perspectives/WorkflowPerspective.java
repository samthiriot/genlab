package genlab.gui.perspectives;

import genlab.gui.views.ExistingView;
import genlab.gui.views.MessagesView;
import genlab.gui.views.WorkflowView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class WorkflowPerspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "genlab.gui.perspectives.WorkflowPerspective";

	
	
	public void createInitialLayout(IPageLayout layout) {
		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		
		// add a messages (console) view in the very bottom

		// add a folder at right...
		IFolderLayout folderBottom = layout.createFolder("bottom folder", IPageLayout.BOTTOM, 0.8f, editorArea);
		folderBottom.addView(MessagesView.ID);
		
		// add a folder at left...
		IFolderLayout folderLeft = layout.createFolder("left folder", IPageLayout.LEFT, 0.15f, editorArea);
		folderLeft.addPlaceholder(MessagesView.ID + ":*");
		// add a navigator
		folderLeft.addView("genlab.gui.views.projectexplorer");
		folderLeft.addView(WorkflowView.ID);
		
		// add a folder at right...
		IFolderLayout folderRight = layout.createFolder("right folder", IPageLayout.RIGHT, 0.85f, editorArea);
		folderRight.addView(ExistingView.ID);
		
		//layout.getViewLayout(NavigatorView.ID).setCloseable(false);
		
	}
	

}
