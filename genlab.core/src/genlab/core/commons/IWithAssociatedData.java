package genlab.core.commons;

/**
 * Objects tagged with this interface may store any kind of data for keys. 
 * If the corresponding object is persisted, this data will be persisted as well.
 * As a consequence, this mechanism can be used along with the events workflow open/closed
 * for plugins to save and restore information.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IWithAssociatedData {


	public Object getObjectForKey(String key);
	
	public Object addObjectForKey(String key, Object object);

	public Object addObjectForKey(String key, Object object, boolean raiseEvent);

	
}
