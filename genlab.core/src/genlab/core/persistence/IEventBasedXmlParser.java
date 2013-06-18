package genlab.core.persistence;

import genlab.core.usermachineinteraction.ListOfMessages;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public interface IEventBasedXmlParser {

	public Object processAttribute(
			String name,
			HierarchicalStreamReader reader,
			UnmarshallingContext ctxt
			);
	

	public Object processSubnode(
			String name,
			HierarchicalStreamReader reader,
			UnmarshallingContext ctxt
			);

	public boolean acceptsAttribute(String name);
	
	public boolean acceptsSubnode(String name, HierarchicalStreamReader reader);
	
	public void terminate(ListOfMessages m);
}
