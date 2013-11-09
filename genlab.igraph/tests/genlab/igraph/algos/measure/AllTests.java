package genlab.igraph.algos.measure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// TODO test that calls of the same library from different threads is working ?

@RunWith(Suite.class)
@SuiteClasses(
		{ 
			TestCentrality.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}