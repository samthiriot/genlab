package genlab.os;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
		{ 
			genlab.os.algos.TestOpenFile.class
			}
		)
public class AllTests {

	public AllTests() {
	}

}
