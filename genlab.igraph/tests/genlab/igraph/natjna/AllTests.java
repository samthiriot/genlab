package genlab.igraph.natjna;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			TestIGraphCallback.class,
			TestIgraphGraphMetrics.class,
			TestIGraphLibrary.class,
			TestIgraphRandomNetworks.class,
			TestRawIgraphRandomness.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}