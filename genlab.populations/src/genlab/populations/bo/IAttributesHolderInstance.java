package genlab.populations.bo;

public interface IAttributesHolderInstance {

	public Object getValueForAttribute(Attribute attribute);
	public Object getValueForAttribute(int idx);
	public Object getValueForAttribute(String attributeName);

	public void setValueForAttribute(Attribute attribute, Object value);
	public void setValueForAttribute(int idx, Object value);
	public void setValueForAttribute(String attributeName, Object value);
	
	
}
