package genlab.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			genlab.core.usermachineinteraction.AllTests.class,
			genlab.core.commons.AllTests.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}
