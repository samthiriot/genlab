package genlab.igraph.natjna;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.Native;

/**
 * A basis for igraph tests which creates a raw library
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractTestIGraphLibrary {

	/**
	 * instance to be tested
	 */
	protected IGraphLibrary lib = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Native.setProtected(true);
		lib = new IGraphLibrary();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testInstance() {
		assertNotNull(lib);
	}
	
	@Test
	public void testVersion() {
		
		String version = lib.getVersionString();
		
		assertNotNull(version);
		
		System.err.println(version);
		
	}

	

}
