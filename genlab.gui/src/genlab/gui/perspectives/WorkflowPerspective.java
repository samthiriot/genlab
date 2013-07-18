package genlab.gui.perspectives;

import genlab.gui.views.ConsoleView;
import genlab.gui.views.ExistingView;
import genlab.gui.views.MessagesView;
import genlab.gui.views.MessagesViewGeneral;
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

		// TODO use extensions instead
		layout.addPlaceholder("genlab.graphstream.ui.views.graphview:*", IPageLayout.TOP, 0.9f, editorArea);
		// add a messages (console) view in the very bottom

		
		// add a folder at bottom...
		IFolderLayout folderBottom = layout.createFolder("bottom folder", IPageLayout.BOTTOM, 0.8f, editorArea);
		folderBottom.addView(MessagesViewGeneral.ID);
		folderBottom.addPlaceholder(MessagesView.ID+":*");
		folderBottom.addPlaceholder(ConsoleView.VIEW_ID+":*");
		
		// add a folder at left...
		IFolderLayout folderLeft = layout.createFolder("left folder", IPageLayout.LEFT, 0.15f, editorArea);
		// add a navigator
		folderLeft.addView("genlab.gui.views.projectexplorer");
		folderLeft.addView(WorkflowView.ID);
		
		// add a folder at right...
		IFolderLayout folderRight = layout.createFolder("right folder", IPageLayout.RIGHT, 0.85f, editorArea);
		folderRight.addView("genlab.gui.views.workflowexplorer");
		folderRight.addView(ExistingView.ID);
		folderRight.addView("genlab.gui.views.AlgoInfoView");
		folderRight.addPlaceholder("genlab.gui.views.ParametersView:*");

		
		//layout.getViewLayout("genlab.gui.views.projectexplorer").
		
	}
	

}
	