package genlab.core.model.meta.basics.vectorsHeterogeneous;

import java.util.Collection;
import java.util.Map;

/**
 * Stores several input data into a "map-vector", 
 * that can be accessed by index or by key.
 * 
 * @author Samuel Thiriot
 */
public interface IHeterogeneousMap {

	/**
	 * returns the number of elements into this vector
	 * @return
	 */
	public int getSize();
	
	public boolean isEmpty();
	
	public Collection<String> getKeys();
	
	public Object getValueForKey(String key);
	
	public Map<String,Object> getKeyToValue();
	
		
	
}
