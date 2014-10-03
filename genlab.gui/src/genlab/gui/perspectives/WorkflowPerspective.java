package genlab.gui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class WorkflowPerspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "genlab.gui.perspectives.WorkflowPerspective";

	public static final String FOLDER_LEFT_ID = ID+".folders.left";
	public static final String FOLDER_RIGHT_ID = ID+".folders.right";
	public static final String FOLDER_BOTTOM_ID = ID+".folders.bottom";
	
	
	public void createInitialLayout(IPageLayout layout) {
		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// TODO use extensions instead
		//layout.addPlaceholder("genlab.graphstream.ui.views.graphview:*", IPageLayout.TOP, 0.9f, editorArea);
		//layout.addPlaceholder("genlab.graphstream.ui.views.graphview2d:*", IPageLayout.TOP, 0.9f, editorArea);

		// add a messages (console) view in the very bottom

		// TODO inspired by http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.6.1/org.eclipse.jdt/ui/3.6.1/org/eclipse/jdt/internal/ui/JavaPerspectiveFactory.java
		
		// add a folder at bottom...
		IFolderLayout folderBottom = layout.createFolder(FOLDER_BOTTOM_ID, IPageLayout.BOTTOM, 0.85f, editorArea);
		//folderBottom.addView(MessagesViewGeneral.ID);
		//folderBottom.addPlaceholder(MessagesView.ID+":*");
		//folderBottom.addPlaceholder(ConsoleView.VIEW_ID+":*");
		
		// add a folder at left...
		IFolderLayout folderLeft = layout.createFolder(FOLDER_LEFT_ID, IPageLayout.LEFT, 0.15f, editorArea);
		// add a navigator
		//folderLeft.addView("genlab.gui.views.projectexplorer");
		//folderLeft.addView(WorkflowView.ID);

		
		// add a folder at right...
		IFolderLayout folderRight = layout.createFolder(FOLDER_RIGHT_ID, IPageLayout.RIGHT, 0.85f, editorArea);
		//folderRight.addView("genlab.gui.views.workflowexplorer");
		//folderRight.addView(ExistingView.ID);
		//folderRight.addView("genlab.gui.views.AlgoInfoView");
		//folderRight.addPlaceholder("genlab.gui.views.ParametersView:*");
		//folderRight.addView(TasksProgressView.VIEW_ID);
		
		//layout.getViewLayout("genlab.gui.views.projectexplorer").
		
		
		//layout.addNewWizardShortcut(
		
	}
	

}
	