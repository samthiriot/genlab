package genlab.populations.persistence;

import genlab.core.persistence.Decoder;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AttributeConverter extends Decoder implements Converter {

	public static final String XML_ATTRIBUTE_ID = "id";
	public static final String XML_ATTRIBUTE_TYPE = "type";

	public AttributeConverter() {
	}

	@Override
	public boolean canConvert(Class c) {
		return Attribute.class.equals(c);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext ctxt) {

		Attribute attribute = (Attribute)value;
		
		writer.addAttribute(XML_ATTRIBUTE_ID, attribute.getID());
		writer.addAttribute(XML_ATTRIBUTE_TYPE, attribute.getType().name());
		
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		final String readenId = (String)reader.getAttribute(XML_ATTRIBUTE_ID);
		final String readenType = (String)reader.getAttribute(XML_ATTRIBUTE_TYPE);
		
		return new Attribute(
				readenId,
				AttributeType.valueOf(readenType)
				);
		
	}

}
