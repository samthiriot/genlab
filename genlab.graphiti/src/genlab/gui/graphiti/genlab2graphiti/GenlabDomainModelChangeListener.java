package genlab.gui.graphiti.genlab2graphiti;

import genlab.gui.graphiti.editors.GenlabDiagramEditor;

/**
 * Listens for updates in the GenLab project, and reflects them in Graphiti... 
 * and the reverse process.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabDomainModelChangeListener {

	private GenlabDiagramEditor editor = null;
	
	public GenlabDomainModelChangeListener(GenlabDiagramEditor editor) {
		this.editor = editor;
		
	}
	

}
