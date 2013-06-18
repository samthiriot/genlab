package genlab.core.persistence;

import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.ListOfMessages;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public abstract class ProcessOnlySubnode implements IEventBasedXmlParser {

	final String nodeName;
	
	public ProcessOnlySubnode(String nodeName) {
		this.nodeName = nodeName;
	}

	@Override
	public Object processAttribute(String name,
			HierarchicalStreamReader reader, UnmarshallingContext ctxt) {

		throw new ProgramException("impossible state");
	}

	
	@Override
	public boolean acceptsAttribute(String name) {
		return false;
	}

	@Override
	public boolean acceptsSubnode(String name, HierarchicalStreamReader reader) {
		return name.equals(nodeName);
	}
	
	@Override
	public void terminate(ListOfMessages m) {
		
	}

}
