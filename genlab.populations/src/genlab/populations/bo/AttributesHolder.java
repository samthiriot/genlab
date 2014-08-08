package genlab.populations.bo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AttributesHolder implements IAttributesHolder {

	private List<Attribute> attributes;
	
	public AttributesHolder() {
		attributes = new LinkedList<Attribute>();
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
	}

	@Override
	public Attribute getAttributeForId(String id) {
		for (Attribute a: attributes) {
			if (a.getID().equals(id))
				return a;
		}
		return null;
	}
	
	
}
