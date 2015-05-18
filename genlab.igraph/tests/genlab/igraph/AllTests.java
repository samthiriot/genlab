package genlab.igraph;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			// TODO restore: genlab.igraph.algos.measure.AllTests.class,
			genlab.igraph.natjna.AllTests.class,
			genlab.igraph.algos.measure.AllTests.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}
