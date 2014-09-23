package genlab.populations.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttributesHolder implements IAttributesHolder {

	private List<Attribute> attributes;
	private HashMap<String,Attribute> id2attribute;
	
	public AttributesHolder() {
		attributes = new LinkedList<Attribute>();
		id2attribute = new LinkedHashMap<String, Attribute>();
	}

	public int getAttributesCount() {
		return attributes.size();
	}
	
	public List<Attribute> getAllAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	@Override
	public void addAttribute(Attribute a) {
		attributes.add(a);
		id2attribute.put(a.id, a);
	}

	@Override
	public Attribute getAttributeForId(String id) {
		return id2attribute.get(id);
	}

	@Override
	public boolean containsAttribute(String id) {
		return id2attribute.containsKey(id);
	}

	@Override
	public List<String> getAllAttributesIds() {
		List<String> ids = new ArrayList<String>(attributes.size());
		for (Attribute a: attributes) {
			ids.add(a.id);
		}
		return ids;
	}
	
	
}
