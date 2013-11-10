package genlab.core.commons;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			TestUniqueTimestamp.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}
