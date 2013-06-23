package genlab.core.persistence;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.GenlabWorkflowInstance;
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
									throw new ProgramException("connection should not be null");
								
								connections.add(connection);
								
								reader.moveUp();

							}
							
							return connections;
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
		}
		
		// post processing on connections (?)
		Collection<Connection> connections = (Collection<Connection>) readen.get("connections");
		for (Connection c: connections) {
			workflow.addConnection(c);
		}

		return workflow;
	}

}
