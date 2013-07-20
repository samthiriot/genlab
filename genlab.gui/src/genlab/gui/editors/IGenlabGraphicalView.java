package genlab.gui.editors;

/**
 * The contract for any GUI view in genlab.
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public interface IGenlabGraphicalView {

	public boolean canSnapshot();
	
	// TODO format ? 
	public boolean canSnapshotNow();

	
	public void snapshot(String filename);
	
}
