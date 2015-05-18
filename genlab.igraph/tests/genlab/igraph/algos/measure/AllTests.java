package genlab.igraph.algos.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// TODO test that calls of the same library from different threads is working ?

@RunWith(Suite.class)
@SuiteClasses(
		{ 
			TestAveragePathLength.class,
			TestComponentCount.class,
			TestDiameter.class,
			TestGiantCluster.class,
			TestGlobalAverageClustering.class,
			TestGlobalClustering.class,
			TestNodeBetweeness.class,
			TestNodeBetweenessEstimates.class,
			TestEdgeBetweeness.class,
			TestEdgeBetweenessEstimate.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}