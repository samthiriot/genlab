import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.graphstream.algos.generators.IGenlabGraphInitializer;
import genlab.graphstream.utils.GraphstreamConvertors;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

import static org.junit.Assert.*;


public class TestGraphstreamNotCreatingThreads extends
		AbstractTestThreadsCreation {

	public TestGraphstreamNotCreatingThreads() {
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
		assertEquals("conversion issue", 50, graph.getVerticesCount());
	}

}
