package genlab.core.persistence;

import genlab.core.model.instance.GenlabWorkflowInstance;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ProjectConverter implements Converter {

	@Override
	public boolean canConvert(Class c) {
		return c.equals(GenlabWorkflowInstance.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctxt) {
		
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext ctxt) {
		// TODO Auto-generated method stub
		return null;
	}

}
