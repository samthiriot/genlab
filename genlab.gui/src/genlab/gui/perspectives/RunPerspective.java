package genlab.gui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The runtime perspective. Only creates folders with unique IDs. 
 * Plugins should use the extension mechanism of plugins to add themselves in "stack" mode
 * inside these folders (just use the folder id as a "relative" element in the extension) 
 * 
 * @author B12772
 *
 */
public class RunPerspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "genlab.gui.perspectives.RunPerspective";
	public static final String FOLDER_RIGHT_ID = ID+".folders.right";
	public static final String FOLDER_MIDDLE_ID = ID+".folders.middle";
	public static final String FOLDER_BOTTOM_ID = ID+".folders.bottom";
	
	
	
	public void createInitialLayout(IPageLayout layout) {
		
		String editorArea = layout.getEditorArea();
		
		// no editor in a run perspective
		layout.setEditorAreaVisible(false);


		// add a folder at bottom...
		IFolderLayout folderBottom = layout.createFolder(FOLDER_BOTTOM_ID, IPageLayout.BOTTOM, 0.85f, editorArea);

		// add a folder at right...
		IFolderLayout folderRight = layout.createFolder(FOLDER_RIGHT_ID, IPageLayout.RIGHT, 0.85f, editorArea);
		
		// the role of the editor area (but for view)
		IFolderLayout folderCentral = layout.createFolder(FOLDER_MIDDLE_ID, IPageLayout.TOP, 0.85f, editorArea);
		
		
	}
	

}
	