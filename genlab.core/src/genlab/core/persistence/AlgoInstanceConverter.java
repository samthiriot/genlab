package genlab.core.persistence;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AlgoInstanceConverter extends Decoder implements Converter {

	public AlgoInstanceConverter() {
	}
	

	@Override
	public boolean canConvert(Class c) {
		return c.equals(AlgoInstance.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctxt) {
		
		final AlgoInstance algo = (AlgoInstance)value;
		
		writer.startNode(GenlabPersistence.XMLTAG_ID);
		writer.setValue(algo.getId());
		writer.endNode();
		
		writer.startNode("algoClassName");
		writer.setValue(algo.getAlgo().getClass().getCanonicalName());
		writer.endNode();
	
		// TODO in the future can of extensible inputs / outputs
		/*
		writer.startNode("inputs");
		for (IInputOutputInstance input : algo.getInputInstances()) {
			writer.startNode("input");
			
			writer.startNode(GenlabPersistence.XMLTAG_ID);
			writer.setValue(input.getId());
			writer.endNode();
			
			writer.startNode("meta");
			writer.setValue(input.getMeta().getId());
			writer.endNode();
			
			writer.endNode();
		}
		writer.endNode();
		*/
		
		if (algo.hasParameters()) {
			writer.startNode("parameters");
			ctxt.convertAnother(algo.getParametersAndValues());
			writer.endNode();
		}
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext ctxt) {
		
		final Map<String,Object> data = this.analyze(
				reader, 
				ctxt,
				new LinkedList<IEventBasedXmlParser>() {{
					add(new StoreExpectedParser(new HashMap<String, Boolean>() {{
						put(GenlabPersistence.XMLTAG_ID, true);
						put("algoClassName", true);
						put("parameters", false);
					}}));
										
				}}
				);
		
		
		final String algoClassName = (String) data.get("algoClassName");
		GLLogger.traceTech("looking for class name "+algoClassName, getClass());
		
		final IAlgo algo = ExistingAlgos.getExistingAlgos().getAlgoForClass(algoClassName);
		GLLogger.traceTech("found the corresponding available algo: "+algo, getClass());
		if (algo == null)
			throw new WrongParametersException("error during the loading of the project: unable to find the algo for class "+algoClassName+"");
			// TODO user friendly message
		
		IAlgoInstance i = algo.createInstance((String)data.get(GenlabPersistence.XMLTAG_ID), null);
				
		return i;
	}

}
