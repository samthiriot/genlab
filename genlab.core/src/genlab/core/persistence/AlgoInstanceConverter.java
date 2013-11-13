package genlab.core.persistence;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class AlgoInstanceConverter extends Decoder implements Converter {



	public AlgoInstanceConverter() {
	}


	@Override
	public boolean canConvert(Class c) {
		return AlgoInstance.class.isAssignableFrom(c);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctxt) {
		
		final AlgoInstance algo = (AlgoInstance)value;
		
		writer.startNode(GenlabPersistence.XMLTAG_ID);
		writer.setValue(algo.getId());
		writer.endNode();
		
		writer.startNode("algoId");
		writer.setValue(algo.getAlgo().getId());
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
	        for (Map.Entry<String, Object> entry : algo._getParametersAndValues().entrySet()) {
	        	writer.startNode("entry");
	        	writer.startNode("key");
	        	writer.setValue(entry.getKey().toString());
	        	writer.endNode();
	            writer.startNode("value");
	            writer.addAttribute("class", entry.getValue().getClass().getCanonicalName());
	            writer.setValue(entry.getValue().toString());
	            writer.endNode();
	            writer.endNode();
	        }

			//ctxt.convertAnother(algo._getParametersAndValues());
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
										put("algoId", true);
										put("algoName", true);
										put("container", false);
									}}
							)
					);
					add(new ProcessOnlySubnode("parameters") {
						
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {

				        							
							if (reader.getNodeName()!="parameters")
				            	throw new WrongParametersException("tag <parameters> expected");
	
					        Map<String, Object> map = new HashMap<String, Object>();

					        while (reader.hasMoreChildren())
					        {
					            reader.moveDown(); // entry

					            try {
						            
						        	if (reader.getNodeName()!="entry")
						            	throw new WrongParametersException("parameters should contain as set of <entry> tags");
	
						        	String key = null;
						        	String value = null;
						            Object parsedValue = null;

						        	String className = null;
						        	
						        	while (reader.hasMoreChildren())
							        {
							        
							            reader.moveDown(); // key or value
							            
							            if (reader.getNodeName() == "key") {
							            	if (key != null)
							            		throw new WrongParametersException("only one <key> tag is expected per <entry>");
							            	key = reader.getValue();					            			
							            } else if (reader.getNodeName() == "value") {
							            	if (value != null)
							            		throw new WrongParametersException("only one <value> tag is expected per <entry>");
								            className = reader.getAttribute("class");
								            if (className == null)
								        		throw new WrongParametersException("A \"class\" attribute is expected for the <value> tags.");
								        	try {
												Class<?> decodingClass = ctxt.getClass().getClassLoader().loadClass(className);
												parsedValue = ctxt.convertAnother(reader, decodingClass);
											} catch (ClassNotFoundException e) {
												throw new WrongParametersException("unable to load the class \""+className+"\" while reading parameter\""+key+"\"");
											}
								        	
							            } else 
							            	throw new WrongParametersException("only one <value> or <key> tags are expected into <entry>");
		
							            reader.moveUp();
							        }
						            
						        	if (key == null || parsedValue == null)
						        		throw new WrongParametersException("<value> and <key> tags are expected into <entry>");
						        	
						        	
						        	
						            map.put(key, parsedValue);
						            
					            } catch (RuntimeException e) {
					            	GLLogger.warnTech("error while attempting to load a parameter for an entity; a value has been lost; please check the parameters.", getClass());
					            }
					            reader.moveUp();

					        }
					        

							return map;
						}
					});
					}}
				);
		
		
		final String algoId = (String) data.get("algoId");
		GLLogger.traceTech("looking for class name "+algoId, getClass());
		
		final IAlgo algo = ExistingAlgos.getExistingAlgos().getAlgoForId(algoId);
		if (algo == null)
			throw new WrongParametersException("error during the loading of the project: unable to find the algo for class "+algoId+"");
			// TODO user friendly message
		GLLogger.traceTech("found the corresponding available algo: "+algo, getClass());

		
		IAlgoInstance i = algo.createInstance((String)data.get(GenlabPersistence.XMLTAG_ID), null);
		final String algoName = (String) data.get("algoName");
		i.setName(algoName);
		
		if (data.get("parameters") != null) {
			
			Map<String,Object> m = (Map<String, Object>)data.get("parameters");;
			for (String s : m.keySet()) {
				try {
					Object value = m.get(s);
					i.setValueForParameter(s, value);
				} catch (RuntimeException e) {
					GLLogger.warnUser("error while loading parameter \""+s+"\" for algo \""+i.getName()+"\" in workflow "+i.getWorkflow().getName()+"; the value for this parameter has been lost", getClass());
				}
			}
		}
		
		return i;
	}

}
