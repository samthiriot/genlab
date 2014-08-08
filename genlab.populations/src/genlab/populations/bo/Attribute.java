package genlab.populations.bo;

public class Attribute {

	protected final String id;
	protected final AttributeType type;
	
	public Attribute(String id, AttributeType type) {
		this.id = id;
		this.type = type;
	}

	public String getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public AttributeType getType() {
		return type;
	}
	
}
