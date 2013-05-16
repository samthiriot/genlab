package genlab.gui.graphiti.editors;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.genlab2graphiti.GenlabDomainModelChangeListener;

import org.eclipse.graphiti.ui.editor.DiagramEditor;

/**
 * Specific diagram editor that does not sync with EMF objects
 * 
 * @author Samuel Thiriot
 */
public class GenlabDiagramEditor extends DiagramEditor {

	public static final String EDITOR_ID = "genlab.gui.graphiti.editors.GenlabDiagramEditor";
	
	private GenlabDomainModelChangeListener domainModelListener = null;
	
	public GenlabDiagramEditor() {
		GLLogger.debugTech("Diagram editor created.", getClass());
	}
	
	@Override
	protected void registerBusinessObjectsListener() {
		domainModelListener = new GenlabDomainModelChangeListener(this);
		// TODO add this as a listener of workflows
	}
	
	@Override
	protected void unregisterBusinessObjectsListener() {
		if (domainModelListener != null) {
			// TODO remove 
			domainModelListener = null;
		}
	}

	
	
}
