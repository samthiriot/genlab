package genlab.populations.bo;

import genlab.core.commons.WrongParametersException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AttributesHolder<EntityClass extends IAttributesHolder<?>> 
					implements IAttributesHolder<EntityClass> {

	private List<Attribute> attributes;
	private HashMap<String,Attribute> id2attribute;
	private Collection<EntityClass> inheritedClasses = null;
	
	public AttributesHolder() {
		attributes = new LinkedList<Attribute>();
		id2attribute = new LinkedHashMap<String, Attribute>();
	}

	public int getLocalAttributesCount() {
		return attributes.size();
	}
	

	@Override
	public int getAllAttributesCount() {
		int res = attributes.size();
		for (EntityClass parent: getInheritedTypes()) {
			res += parent.getAllAttributesCount();
		}
		return res;
	}

	
	public List<Attribute> getAllAttributes() {
		List<Attribute> res = new LinkedList<Attribute>(attributes);
		for (EntityClass parent: getInheritedTypes()) {
			res.addAll(parent.getAllAttributes());
		}
		return res;
	}

	@Override
	public void addAttribute(Attribute a) {
		
		// ensure the attribute ID was not declared already
		{
			IAttributesHolder<?> parent = this.getTypeDeclaringAttribute(a.getID());
			if (parent != null)
				throw new WrongParametersException(
						"unable to add an attribute "+a.getID()+" to type "+this+": "
						+ "an attribute "+a.getID()+" is already defined in the parent class "+parent
						);
		}
		
		attributes.add(a);
		id2attribute.put(a.id, a);
	}

	@Override
	public Attribute getAttributeForId(String id) {
		Attribute res = id2attribute.get(id);
		if (res != null)
			return res;
		for (EntityClass parent: getInheritedTypes()) {
			res = parent.getAttributeForId(id);
			if (res != null)
				return res;
		}
		return null;
	}

	@Override
	public boolean containsAttribute(String id) {
		if (id2attribute.containsKey(id))
			return true;
		
		for (EntityClass parent: getInheritedTypes()) {
			if (parent.containsAttribute(id))
					return true;
		}
		
		return false;
	}

	@Override
	public List<String> getAllAttributesIds() {
		List<String> ids = new ArrayList<String>(attributes.size());
		for (Attribute a: attributes) {
			ids.add(a.id);
		}
		for (EntityClass parent: getInheritedTypes()) {
			ids.addAll(parent.getAllAttributesIds());
		}
		return ids;
	}

	protected final Collection<EntityClass> getOrCreateEntityClasses() {
		if (inheritedClasses == null)
			inheritedClasses = new LinkedList<EntityClass>();
		return inheritedClasses;
	}
	
	@Override
	public Collection<EntityClass> getInheritedTypes() {
		if (inheritedClasses == null)
			return Collections.EMPTY_LIST;
		else
			return Collections.unmodifiableCollection(inheritedClasses);
	}

	@Override
	public void addInheritedTypes(EntityClass inheritedClass)
			throws WrongParametersException {
		
		getOrCreateEntityClasses();

		// ensure there is no loop
		{
			Set<IAttributesHolder<?>> parentsIdentified = new HashSet<IAttributesHolder<?>>();
			parentsIdentified.add(this);
			Set<IAttributesHolder<?>> toExplore = new HashSet<IAttributesHolder<?>>();
			toExplore.add(inheritedClass);
			while (toExplore.iterator().hasNext()) {
				
				Iterator<IAttributesHolder<?>> itAtt = toExplore.iterator();
				IAttributesHolder<?> current = itAtt.next();
				itAtt.remove();
				
				if (parentsIdentified.contains(current)) {
					throw new WrongParametersException("Unable to add this inheritance: it would create a loop.");
				}
				
				parentsIdentified.add(current);
	
				// add the parents for exploration
				for (IAttributesHolder<?> parent: current.getInheritedTypes()) {
					if (parentsIdentified.contains(parent)) {
						throw new WrongParametersException(
								"Unable to add this inheritance: it would create a loop ("
								+current+" inherits "+parent+")"
								);
					}
					toExplore.add(parent);
				}
				
			}
			
		}
		
		// check there is no attribute conflict
		for (String localAttributeId: id2attribute.keySet()) {
			IAttributesHolder<?> type = inheritedClass.getTypeDeclaringAttribute(localAttributeId);
			if (type != null)
				throw new WrongParametersException(
						"unable to make "+this+" inherit "+inheritedClass+": "
						+ "the attribute "+localAttributeId+" defined by the parent type "+type+" is redefined in type "+this
						);
		}
		
		// add it !
		if (!inheritedClasses.contains(inheritedClass))
			inheritedClasses.add(inheritedClass);
	}

	@Override
	public List<String> getLocalAttributesIds() {
		List<String> ids = new ArrayList<String>(attributes.size());
		for (Attribute a: attributes) {
			ids.add(a.id);
		}
		return ids;
	}

	@Override
	public List<Attribute> getLocalAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	@Override
	public IAttributesHolder<?> getTypeDeclaringAttribute(String attributeId) {
		
		// maybe it is me ? 
		if (id2attribute.containsKey(attributeId))
			return this;
		
		// maybe it is one of my parents ? 
		IAttributesHolder<?> res = null;
		for (EntityClass parent: getInheritedTypes()) {
			res = parent.getTypeDeclaringAttribute(attributeId);
			if (res != null)
				return res;
		}
		
		// or there is none !
		return null;
	}

	
}
