package genlab.jung.utils;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;


public class JungWriters {

	public static void writeGraphAsGraphML(final IGenlabGraph graph, File fileTo) {
		
		Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphReadonly(graph);
		GraphMLWriter<String, String> graphWriter = new GraphMLWriter<String, String>();
		// add node attributes
		for (Entry<String, Class> e : graph.getDeclaredVertexAttributesAndTypes().entrySet()) {
			
			final String attributeId = e.getKey();
			
		
			graphWriter.addVertexData(
					e.getKey(), 
					null, 
					"NaN", 
					new Transformer<String,String>() {

						@Override
						public String transform(String vertexId) {
							return graph.getVertexAttributeValue(vertexId, attributeId).toString();
						}										
					}
			);
		
			
		}
		
		// add edge attributes
		for (Entry<String, Class> e : graph.getDeclaredEdgeAttributesAndTypes().entrySet()) {
			
			final String attributeId = e.getKey();
								
			graphWriter.addEdgeData(
					e.getKey(), 
					null, 
					"NaN", 
					new Transformer<String,String>() {

						@Override
						public String transform(String vertexId) {
							return graph.getEdgeAttributeValue(vertexId, attributeId).toString();
						}										
					}
			);
			
		}
		
		try {
			PrintWriter out = new PrintWriter(fileTo);
			graphWriter.save(jungGraph, out);
		} catch (IOException e1) {
			throw new RuntimeException("error while writing network to disk: "+e1.getMessage(), e1);
		}
	}
	private JungWriters() {

	}

}
