package genlab.core.model.instance;

/**
 * Describes listeners interested in the workflow content (something added,
 * something removed...)
 * 
 * @author Samuel Thiriot
 *
 */
public interface IWorkflowContentListener {

	public void notifyConnectionAdded(IConnection c);
	
	public void notifyConnectionRemoved(IConnection c);
	
	public void notifyAlgoAdded(IAlgoInstance ai);
	
	public void notifyAlgoRemoved(IAlgoInstance ai);
	
	public void notifyAlgoChanged(IAlgoInstance ai);

}
