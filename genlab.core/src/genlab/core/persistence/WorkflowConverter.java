package genlab.core.persistence;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class WorkflowConverter extends Decoder implements Converter {
	
	@Override
	public boolean canConvert(Class c) {
		return c.equals(GenlabWorkflowInstance.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext ctxt) {
		
		final GenlabWorkflowInstance workflow = (GenlabWorkflowInstance)value;
		
		writer.addAttribute("id", workflow.getId());
		
		writer.startNode("name");
		writer.setValue(workflow.getName());
		writer.endNode();
		
		writer.startNode("description");
		writer.setValue(workflow.getDescription());
		writer.endNode();
		
		writer.startNode("subalgos");
		for (IAlgoInstance i: workflow.getAlgoInstances()) {
			
			
			writer.startNode("algo");
			
            ctxt.convertAnother(i);

/*			writer.addAttribute("id", i.getId());
			
			writer.startNode("class");
			writer.setValue(i.getAlgo().getClass().getCanonicalName());
			writer.endNode();
			*/
			writer.endNode();
		}
		writer.endNode();
		

		writer.startNode("connections");

		for (Connection c : workflow.getConnections() ) {
		
			writer.startNode(GenlabPersistence.XMLTAG_CONNECTIONINSTANCE);
		
            ctxt.convertAnother(c);

			writer.endNode();
		}
		writer.endNode();
		
		writer.startNode("inclusions");
		for (IAlgoInstance ai : workflow.getAlgoInstances()) {
			if (ai.getContainer() == null)
				continue;
			writer.startNode("contained");
			writer.addAttribute("put", ai.getId());
			writer.addAttribute("into", ai.getContainer().getId());
			writer.endNode();
		}
		writer.endNode();
		
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext ctxt) {

		final Map<String,Object> readen = analyze(
				reader, 
				ctxt,
				new LinkedList<IEventBasedXmlParser>() {{
					add(new StoreExpectedParser(new HashMap<String, Boolean>() {{
						put("id", Boolean.TRUE);
						put("name", Boolean.TRUE);
						put("description", Boolean.FALSE);	
					}}));
					add(new ProcessOnlySubnode("subalgos") {
							
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {

							GLLogger.debugTech("decoding subalgos for this workflow", getClass());
							
							
							final Collection<IAlgoInstance> algos = new LinkedList<IAlgoInstance>();
							
							while (reader.hasMoreChildren()) {
								reader.moveDown();

								if (reader.getNodeName() != GenlabPersistence.XMLTAG_ALGOINSTANCE) {
									GLLogger.warnTech("ignored tag in algo: "+reader.getNodeName(), getClass());
									continue;
								}
								IAlgoInstance algoInstance = (IAlgoInstance) ctxt.convertAnother(reader, AlgoInstance.class);
								if (algoInstance == null)
									throw new ProgramException("algo instance should not be null");
								algos.add(
										algoInstance
										);	
								// TODO declare !
								GenlabPersistence.getPersistence().addCurrentAlgoInstance(algoInstance);

								reader.moveUp();

							}
							
							
							return algos;
						}
					});
					add(new ProcessOnlySubnode("connections") {
						
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {

							GLLogger.debugTech("decoding connections for this workflow", WorkflowConverter.class);
							
							final Collection<IConnection> connections = new LinkedList<IConnection>();
							
							while (reader.hasMoreChildren()) {
								reader.moveDown();

								if (reader.getNodeName() != GenlabPersistence.XMLTAG_CONNECTIONINSTANCE) {
									GLLogger.warnTech("ignored tag in algo: "+reader.getNodeName(), WorkflowConverter.class);
									continue;
								}
								IConnection connection = (IConnection) ctxt.convertAnother(reader, Connection.class);
								if (connection == null)
									GLLogger.warnTech("a connection was not loaded", getClass());
								else 
									connections.add(connection);
								
								reader.moveUp();

							}
							
							return connections;
						}
					});
					add(new ProcessOnlySubnode("inclusions") {
						
						@Override
						public Object processSubnode(String name, HierarchicalStreamReader reader,
								UnmarshallingContext ctxt) {
						
							GLLogger.debugTech("decoding inclusions for this workflow", WorkflowConverter.class);

							Map<String,String> child2parentId = new HashMap<String, String>();
							
							while (reader.hasMoreChildren()) {
								reader.moveDown();

								if (reader.getNodeName() != "contained") {
									GLLogger.warnTech("ignored tag in algo: "+reader.getNodeName(), WorkflowConverter.class);
									continue;
								}
								final String childId = reader.getAttribute("put");
								final String containerId = reader.getAttribute("into");
								
								child2parentId.put(childId, containerId);
								
								reader.moveUp();

							}
							
							return child2parentId;
						}
					});
				}}
				);
		
	
		final IGenlabProject project = GenlabPersistence.getPersistence().getCurrentProject();
		final String filename = GenlabPersistence.getPersistence().getCurrentWorkflowFilename();
		
		if (project == null || filename == null)
			throw new ProgramException("project or filename should not be null");
		
		GenlabWorkflowInstance workflow = new GenlabWorkflowInstance(
				(String)readen.get("id"),
				project,
				(String)readen.get("name"),
				(String)readen.get("description"),
				filename
				);
				
		// post processing on algos
		Collection<IAlgoInstance> algos = (Collection<IAlgoInstance>) readen.get("subalgos");
		for (IAlgoInstance a: algos) {
			a._setWorkflowInstance(workflow);
			workflow.addAlgoInstance(a);
		}
		
		// post processing on connections (?)
		Collection<Connection> connections = (Collection<Connection>) readen.get("connections");
		if (connections != null) {
			for (Connection c: connections) {
				workflow.addConnection(c);
			}
		}
		
		// post processing on inclusions
		Map<String,String> child2parentId = (Map<String,String>)readen.get("inclusions");
		if (child2parentId != null) {
			for (String childId : child2parentId.keySet()) {
				String containerId = child2parentId.get(childId);
				IAlgoInstance aiChild = workflow.getAlgoInstanceForId(childId);
				IAlgoContainerInstance aiContainer = (IAlgoContainerInstance)workflow.getAlgoInstanceForId(containerId);
				if (aiChild == null)
					throw new WrongParametersException("unable to find a children for "+childId);
				if (aiContainer == null)
					throw new WrongParametersException("unable to find a container for "+containerId);
				aiChild.setContainer((IAlgoContainerInstance) aiContainer);
				aiContainer.addChildren(aiChild);
			}
		}
		return workflow;
	}

}
