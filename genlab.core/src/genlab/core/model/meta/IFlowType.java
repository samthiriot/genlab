package genlab.core.model.meta;


/**
 * 
 * Describes a flow (like a network, a file, an integer, etc.)
 * 
 * @author Samuel Thiriot
 */
public interface IFlowType<JavaType> {

	public String getId();
	
	public String getShortName();
	
	public String getDescription();
	
	public String getHtmlDescription();
	
	public JavaType decodeFrom(Object value);

	public boolean compliantWith(IFlowType<?> other);

}
