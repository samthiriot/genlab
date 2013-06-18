package genlab.core.persistence;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AbstractFlowType;
import genlab.core.model.meta.basics.flowtypes.ExistingFlowTypes;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class InputOutputConverter implements Converter {

	public InputOutputConverter() {
	}

	@Override
	public boolean canConvert(Class c) {
		return c.equals(InputOutput.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctx) {
		
		InputOutput io = (InputOutput)value;
		
		writer.addAttribute("id", io.getId());

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext ctx) {
		
		final String id = reader.getAttribute("id");
		AbstractFlowType t = ExistingFlowTypes.getType(id);

		if (t==null)
			throw new RuntimeException("unable to find a flow type for id "+id);
		return t;
	}

}
