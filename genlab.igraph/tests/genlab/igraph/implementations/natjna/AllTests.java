package genlab.igraph.implementations.natjna;

import genlab.core.usermachineinteraction.ListOfMessages;

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
		ListOfMessages.DEFAULT_RELAY_TO_LOG4J = true;
		
	}

}