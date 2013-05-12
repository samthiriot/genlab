package genlab.graphiti.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class GraphitiEditor extends TextEditor {

	private ColorManager colorManager;

	public GraphitiEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
