package genlab.core.persistence;

import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Receives any event, and stores its result.
 * 
 * @author Samuel Thiriot
 *
 */
public class StoreEverythingParser implements IEventBasedXmlParser {

	public final Map<String, Object> key2value = new HashMap<String, Object>();
	
	public StoreEverythingParser() {
	
	}


	@Override
	public Object processAttribute(String name, HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		final Object value = reader.getAttribute(name);
		key2value.put(name, value);
		return value;
	}

	@Override
	public Object processSubnode(String name, HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		final Object value = reader.getValue();
		key2value.put(name, value);

		return value;
	}


	@Override
	public boolean acceptsAttribute(String name) {
		return true;
	}


	@Override
	public boolean acceptsSubnode(String name, HierarchicalStreamReader reader) {
		return true;
	}


	@Override
	public void terminate(ListOfMessages m) {
		
	}
	

}
