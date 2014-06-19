package genlab.testing.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			// tests defined by this pluging
			genlab.testing.referenceWorkflows.TestSimpleGraphGeneratorChains.class,
			genlab.testing.referenceWorkflows.LoopedGraphGeneratorChains.class,
			
			tests.genlab.core.persistence.TestPersistenceChains.class,
			
			// tests from other plugins
			
			// genetic algos
			genlab.algog.testing.TestAlgoGNetwork.class,

			// genlab.igraph.AllTests.class,
			
			
			}
		)
public class AllTests {

	public AllTests() {
	}

}
