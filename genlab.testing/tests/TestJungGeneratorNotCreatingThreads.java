import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.graphstream.algos.generators.IGenlabGraphInitializer;
import genlab.graphstream.utils.GraphstreamConvertors;
import genlab.jung.utils.Converters;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import static org.junit.Assert.*;


public class TestJungGeneratorNotCreatingThreads extends
		AbstractTestThreadsCreation {

	public TestJungGeneratorNotCreatingThreads() {
	}

	@Override
	protected void runTaskToCheck() {

		BaseGenerator generator = new WattsStrogatzGenerator(50, 2, 0.1);
		IGenlabGraph graph = GraphstreamConvertors.loadGraphWithGraphstreamFromGeneratorSource(
				"g1", 
				generator,
				-1,
				null,
				false,
				GraphDirectionality.UNDIRECTED,
				new IGenlabGraphInitializer() {
					
					@Override
					public void initGraph(IGenlabGraph glGraph) {
						glGraph.declareVertexAttribute("x", Integer.class);
						glGraph.declareVertexAttribute("y", Integer.class);
					}
				}
				);
		Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphReadonly(graph);
		DistanceStatistics.diameter(jungGraph); 
		
		assertEquals("conversion issue", 50, jungGraph.getVertexCount());
	}

}
