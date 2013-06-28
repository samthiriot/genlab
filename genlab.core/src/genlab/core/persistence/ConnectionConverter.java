package genlab.core.persistence;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ConnectionConverter extends Decoder implements Converter {

	public ConnectionConverter() {
	}


	@Override
	public boolean canConvert(Class c) {
		return c.equals(Connection.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctxt) {
		
		final Connection c = (Connection)value;

		writer.startNode("glid");
		writer.setValue(c.getId());
		writer.endNode();

		writer.startNode("from");
		writer.setValue(c.getFrom().getId());
		writer.endNode();
		
		writer.startNode("to");
		writer.setValue(c.getTo().getId());
		writer.endNode();
	
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		try {
			final Map<String,Object> data = this.analyze(
					reader, 
					ctxt,
					new LinkedList<IEventBasedXmlParser>() {{
						add(new StoreExpectedParser(new HashMap<String, Boolean>() {{
							put("glid", true);
							put("from", true);
							put("to", true);
						}}));
											
					}}
					);
			
	
			String id = (String)data.get("glid");
			if (id == null)
				throw new WrongParametersException("no glid defined: "+(String)data.get("glid"));
			IInputOutputInstance aFrom = GenlabPersistence.getPersistence().getCurrentIOInstance((String)data.get("from"));
			if (aFrom == null)
				throw new WrongParametersException("unable to use this algo instance which was not yet declared: "+(String)data.get("from"));
			IInputOutputInstance aTo = GenlabPersistence.getPersistence().getCurrentIOInstance((String)data.get("to"));
			if (aTo == null)
				throw new WrongParametersException("unable to use this algo instance which was not yet declared: "+(String)data.get("to"));
			
			Connection c = new Connection(id, aFrom, aTo);
			aFrom.addConnection(c);
			aTo.addConnection(c);
			
			return c;
			
		} catch (RuntimeException e) {
			GLLogger.errorTech("error while reading the file: a connection could not be created. The project will be partial.", getClass());
			return null;
		}
	}

}
