package genlab.core.persistence;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class StoreExpectedParser implements IEventBasedXmlParser {

	
	private Map<String,Boolean> key2mandatory;
	private Map<String,Object> key2value;
	
	/**
	 * If object is boolean true, value is decoded as string 
	 * and is mandatory. If boolean and false, decoded as string 
	 * and optional. It instance of class, delegation to this class.
	 * @param key2mandatory
	 */
	public StoreExpectedParser(Map<String,Boolean> key2mandatory) {

		this.key2mandatory = key2mandatory;
		this.key2value = new HashMap<String, Object>(key2mandatory.size());
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

		Object value = null;
		
		value = reader.getValue();
		key2value.put(name, value);
		
		return value;
	}

	@Override
	public boolean acceptsAttribute(String name) {
		return key2mandatory.containsKey(name);
	}

	@Override
	public boolean acceptsSubnode(String name, HierarchicalStreamReader reader) {
		return key2mandatory.containsKey(name);
	}
	
	/**
	 * ensures that everything required is ok
	 */
	public void terminate(ListOfMessages m) {
		final Set<String> notFound = new HashSet<String>();
		for (String s: key2mandatory.keySet()) {
			if (key2mandatory.get(s) && !key2value.containsKey(s)) {
				notFound.add(s);
				m.errorTech("not found an expected attribute or value: "+s, getClass());
			} 
		}
		if (!notFound.isEmpty())
			throw new WrongParametersException("not found expected attribute or nodes: "+notFound);
	}

}
