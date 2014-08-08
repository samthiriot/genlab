package genlab.populations.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import genlab.core.commons.ProgramException;
import genlab.core.persistence.Decoder;
import genlab.core.persistence.IEventBasedXmlParser;
import genlab.core.persistence.ProcessOnlySubnode;
import genlab.core.persistence.StoreExpectedParser;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgentType;
import genlab.populations.implementations.basic.AgentType;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Persistence of AgentType as XML
 * 
 * @author Samuel Thiriot
 *
 */
public class AgentTypeConverter extends Decoder implements Converter {

	public static final String XML_ATTRIBUTE_NAME = "name";
	public static final String XML_TAG_ATTRIBUTES = "attributes";
	public static final String XML_TAG_ATTRIBUTE = "attribute";
	
	public AgentTypeConverter() {
	}

	@Override
	public boolean canConvert(Class arg0) {
		return IAgentType.class.isAssignableFrom(arg0);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext ctxt) {

		IAgentType type = (IAgentType)value;
		
		writer.addAttribute(XML_ATTRIBUTE_NAME, type.getName());
		
		writer.startNode(XML_TAG_ATTRIBUTES);
		
		for (Attribute a: type.getAllAttributes()) {
			writer.startNode(XML_TAG_ATTRIBUTE);
			ctxt.convertAnother(a);
			writer.endNode();
		}
		
		writer.endNode();
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {

		final Map<String,Object> readen = analyze(
				reader, 
				ctxt,
				new LinkedList<IEventBasedXmlParser>() {{
					add(new StoreExpectedParser(new HashMap<String, Boolean>() {{
						put(XML_ATTRIBUTE_NAME, Boolean.TRUE);	
					}}));
					add(new ProcessOnlySubnode(XML_TAG_ATTRIBUTES) {
							
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {

							GLLogger.debugTech("decoding attributes for this type", getClass());
							
							final Collection<Attribute> attributes = new LinkedList<Attribute>();
							
							while (reader.hasMoreChildren()) {
								reader.moveDown();

								if (reader.getNodeName() != XML_TAG_ATTRIBUTE) {
									GLLogger.warnTech("ignored tag in types: "+reader.getNodeName(), getClass());
									continue;
								}
								Attribute a = (Attribute)ctxt.convertAnother(reader, Attribute.class);
								if (a == null)
									throw new ProgramException("attribute should not be null");
								attributes.add(
										a
										);	
								
								reader.moveUp();

							}
							
							
							return attributes;
						}
					});
				}}
				);
		
		
		AgentType type = new AgentType(
				(String)readen.get(XML_ATTRIBUTE_NAME), 
				null
				);
		
		for (Attribute a: (List<Attribute>)readen.get(XML_TAG_ATTRIBUTES)) {
			type.addAttribute(a);
		}
		
		return type;
		
		
	}

}
