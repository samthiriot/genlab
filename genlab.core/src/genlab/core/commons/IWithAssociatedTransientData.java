package genlab.core.commons;

/**
 * Objects tagged with this interface may store any kind of data for keys. 
 * If the corresponding object is persisted, this data will NOT be persisted.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IWithAssociatedTransientData {


	public Object getTransientObjectForKey(String key);
	
	public Object addTransientObjectForKey(String key, Object object);

	
}
