package genlab.core.persistence;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class Decoder {

	public Decoder() {
		
	}
	
	
	protected  Map<String,Object> analyze(
			HierarchicalStreamReader reader,
			UnmarshallingContext ctxt,
			List<IEventBasedXmlParser> parsers
			) {
		
		GLLogger.traceTech("decoding node "+reader.getNodeName(), getClass());
		
		final  Map<String,Object> results = new HashMap<String, Object>();
		final List<String> ignored = new LinkedList<String>();
		
		// decode attributes
		{
			GLLogger.traceTech("count of attributes "+reader.getAttributeCount(), getClass());
			
			final Iterator<String> itAttributes = reader.getAttributeNames();
			att: while (itAttributes.hasNext()) {
				String n = itAttributes.next();
				
				GLLogger.traceTech("found attribute "+n, getClass());
				
				for (IEventBasedXmlParser parser: parsers) {
					if (parser.acceptsAttribute(n)) {
						results.put(
								n, 
								parser.processAttribute(n, reader, ctxt)
								);
						continue att;
					}
				}

				ignored.add(n);
			}
		}
		
		// decode subnodes
		{
			subnodes : while (reader.hasMoreChildren()) {
				reader.moveDown();
				GLLogger.traceTech("processing subnode "+reader.getNodeName(), getClass());
	
				for (IEventBasedXmlParser parser: parsers) {
					if (parser.acceptsSubnode(reader.getNodeName(), reader)) {
						results.put(
								reader.getNodeName(),
								parser.processSubnode(reader.getNodeName(), reader, ctxt)
								);
						reader.moveUp();
						continue subnodes;
					}
				}
				ignored.add(reader.getNodeName());
				
				reader.moveUp();
			}
		}
		
		// terminate
		for (IEventBasedXmlParser parser: parsers) {
			parser.terminate(ListsOfMessages.getGenlabMessages());
		}
		
		// notify of what was ignored
		if (!ignored.isEmpty())
			GenlabPersistence.getPersistence().getMessages().warnTech(
					"during the decoding, several values were ignored: "+ignored, 
					getClass()
					);
		
		
		return results;
	}

}
