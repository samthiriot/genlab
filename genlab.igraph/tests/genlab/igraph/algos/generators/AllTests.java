package genlab.igraph.algos.generators;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// TODO test that calls of the same library from different threads is working ?

@RunWith(Suite.class)
@SuiteClasses(
		{ 
			TestBarabasiAlbertDirected.class,
			TestBarabasiAlbertUndirected.class,
			TestERGNMDirected.class,
			TestERGNMUndirected.class,
			TestERGNPDirected.class,
			TestERGNPUndirected.class,
			TestForestFireDirected.class,
			TestForestFireUndirected.class,
			TestGRG.class,
			TestSSI.class,
			TestWattsStrogatz.class,
			}
		)
public class AllTests {

	public AllTests() {
	}

}
