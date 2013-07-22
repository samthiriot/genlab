package genlab.core.persistence;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
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
		
		writer.startNode("algoName");
		writer.setValue(algo.getName());
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
					add(
							new StoreExpectedParser(
									new HashMap<String, Boolean>() {{
										put(GenlabPersistence.XMLTAG_ID, true);
										put("algoClassName", true);
										put("algoName", true);
										put("container", false);
									}}
							)
					);
					add(new ProcessOnlySubnode("parameters") {
						
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {
							
							reader.moveDown();
							if (!reader.getNodeName().equals("m"))
								throw new WrongParametersException("malformed xml: 'm' expected");

							Map<String,Object> map = (Map<String, Object>) ctxt.convertAnother(reader, HashMap.class);

							reader.moveUp();
							return map;
						}
					});
					}}
				);
		
		
		final String algoClassName = (String) data.get("algoClassName");
		GLLogger.traceTech("looking for class name "+algoClassName, getClass());
		
		final IAlgo algo = ExistingAlgos.getExistingAlgos().getAlgoForClass(algoClassName);
		if (algo == null)
			throw new WrongParametersException("error during the loading of the project: unable to find the algo for class "+algoClassName+"");
			// TODO user friendly message
		GLLogger.traceTech("found the corresponding available algo: "+algo, getClass());

		IAlgoInstance i = algo.createInstance((String)data.get(GenlabPersistence.XMLTAG_ID), null);
		
		final String algoName = (String) data.get("algoName");
		i.setName(algoName);
		
		if (data.get("parameters") != null) {
			
			Map<String,Object> m = (Map<String, Object>)data.get("parameters");;
			for (String s : m.keySet()) {
				i.setValueForParameter(s, m.get(s));
			}
		}
		
		return i;
	}

}
