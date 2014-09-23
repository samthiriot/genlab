package genlab.populations.bo;

import java.util.Collection;
import java.util.List;

public interface IAttributesHolder {

	public int getAttributesCount();
	
	public boolean containsAttribute(String id);

	public List<String> getAllAttributesIds();
	public List<Attribute> getAllAttributes();
	public void addAttribute(Attribute a);

	public Attribute getAttributeForId(String id);


}
