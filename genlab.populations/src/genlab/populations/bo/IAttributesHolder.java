package genlab.populations.bo;

import java.util.List;

public interface IAttributesHolder<EntityClass extends IAttributesHolder<?>> 
								extends IMultipleInheritance<EntityClass> {

	/**
	 * Returns how many attributes are defined there (including parent attributes)
	 * @return
	 */
	public int getAllAttributesCount();
	
	public int getLocalAttributesCount();
	
	/**
	 * Returns true if this class, or parents, contain this attribute
	 * @param id
	 * @return
	 */
	public boolean containsAttribute(String id);

	/**
	 * returns the list of all attributes ids (including parents' attributes)
	 * @return
	 */
	public List<String> getAllAttributesIds();
	
	/**
	 * returns the list of local attributes ids (excluding parents' attributes)
	 * @return
	 */
	public List<String> getLocalAttributesIds();
	
	/**
	 * Returns all the attributes (including parents' attributes)
	 * @return
	 */
	public List<Attribute> getAllAttributes();
	

	/**
	 * Returns the local attributes (excluding parents' attributes)
	 * @return
	 */
	public List<Attribute> getLocalAttributes();
	
	/**
	 * Adds an attribute to this type
	 * @param a
	 */
	public void addAttribute(Attribute a);

	/**
	 * Returns the attribute for this id (local or from one parent)
	 * @param id
	 * @return
	 */
	public Attribute getAttributeForId(String id);

	/**
	 * Returns the type (this instance of one of its parent) declaring the attribute, or null if none.
	 * @param attributeId
	 * @return
	 */
	public IAttributesHolder<?> getTypeDeclaringAttribute(String attributeId);

}
