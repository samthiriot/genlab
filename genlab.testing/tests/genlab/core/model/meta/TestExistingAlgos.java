package genlab.core.model.meta;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO to be changed to a generic test
 * 
 * @author B12772
 *
 */
public class TestExistingAlgos {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExistingAlgos() {
		
		Collection<IAlgo> algos = ExistingAlgos.getExistingAlgos().getAlgos();
		
		assertNotNull(ExistingAlgos.getExistingAlgos());
		
		assertNotNull(algos);
		
		for (IAlgo algo: algos) {
			
			assertNotNull(algo.getName());
			
			assertNotNull(algo.getInputs());
			assertNotNull(algo.getOuputs());
			
			assertTrue(ExistingAlgos.getExistingAlgos().getAlgoNames().contains(algo.getName()));
			
			// TODO attempt to create an instance
			
		}
		
	}

}
