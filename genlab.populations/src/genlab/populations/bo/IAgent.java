package genlab.populations.bo;

public interface IAgent {

	public Object getId();
	
	public Object getValueForAttribute(Attribute attribute);
	
	public Object getValueForAttribute(int idx);

	public void setValueForAttribute(Attribute attribute, Object value);
	public void setValueForAttribute(int idx, Object value);
	
}
