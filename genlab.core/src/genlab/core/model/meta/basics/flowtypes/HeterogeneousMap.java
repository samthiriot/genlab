package genlab.core.model.meta.basics.flowtypes;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Associates data with string keys.
 * 
 * @author Samuel Thiriot
 *
 */
public class HeterogeneousMap implements IHeterogeneousMap {

	/**
	 * Maps key and object
	 */
	private Map<String, Object>  key2value = new HashMap<String, Object>();
	
	public HeterogeneousMap() {
		
	}
	
	@Override
	public int getSize() {
		return key2value.size();
	}

	@Override
	public boolean isEmpty() {
		return key2value.isEmpty();
	}

	@Override
	public Collection<String> getKeys() {
		return key2value.keySet();
	}

	@Override
	public Object getValueForKey(String key) {
		return key2value.get(key);
	}

	@Override
	public Map<String, Object> getKeyToValue() {
		return Collections.unmodifiableMap(key2value);
	}

	@Override
	public void put(String key, Object value) {
		key2value.put(key, value);
	}

}
