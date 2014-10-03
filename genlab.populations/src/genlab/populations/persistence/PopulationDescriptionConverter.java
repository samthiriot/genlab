package genlab.populations.persistence;

import genlab.core.commons.ProgramException;
import genlab.core.persistence.Decoder;
import genlab.core.persistence.IEventBasedXmlParser;
import genlab.core.persistence.ProcessOnlySubnode;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.PopulationDescription;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class PopulationDescriptionConverter extends Decoder implements Converter {

	public static final String XML_TAG_TYPES = "types";
	public static final String XML_TAG_TYPE = "type";
	
	public static final Object KEY_INTERNAL_AVAILABLE_AGENTYPES = new Object();
	
	
	public PopulationDescriptionConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canConvert(Class c) {
		return PopulationDescription.class.isAssignableFrom(c);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext ctxt) {

		final PopulationDescription popDesc = (PopulationDescription)value;
		
		// add the types
		writer.startNode(XML_TAG_TYPES);
		for (IAgentType type: popDesc.getAllAgentTypes()) {
			writer.startNode(XML_TAG_TYPE);
			ctxt.convertAnother(type);
			writer.endNode();
		}
		writer.endNode();
		
		// TODO aggregation
		// popDesc.getAggregationRelationship()
		
		// TODO links
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		final Map<String,Object> readen = analyze(
				reader, 
				ctxt,
				new LinkedList<IEventBasedXmlParser>() {{
					add(new ProcessOnlySubnode(XML_TAG_TYPES) {
							
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {

							GLLogger.debugTech("decoding types for this population description", getClass());
							
							final Collection<IAgentType> types = new LinkedList<IAgentType>();
							final Map<String,IAgentType> id2type = new HashMap<String, IAgentType>();
							
							while (reader.hasMoreChildren()) {
								reader.moveDown();

								if (reader.getNodeName() != XML_TAG_TYPE) {
									GLLogger.warnTech("ignored tag in types: "+reader.getNodeName(), getClass());
									continue;
								}
								IAgentType type = (IAgentType)ctxt.convertAnother(reader, IAgentType.class);
								if (type == null)
									throw new ProgramException("type should not be null");
								types.add(
										type
										);	
								id2type.put(type.getName(), type);
								ctxt.put(KEY_INTERNAL_AVAILABLE_AGENTYPES, id2type);
								
								reader.moveUp();

							}
							
							
							return types;
						}
					});
					// TODO decode links and aggregation
				}}
				);
		
		PopulationDescription popDesc = new PopulationDescription();
		for (IAgentType type: (Collection<IAgentType>)readen.get(XML_TAG_TYPES)) {
			popDesc.addAgentType(type);
			
		}
		// TODO add links and aggregation
		
		return popDesc;
	}

}
