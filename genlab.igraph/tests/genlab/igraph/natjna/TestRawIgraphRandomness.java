package genlab.igraph.natjna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * test the random features of the raw Igraph JNA library 
 * 
 * @author Samuel Thiriot
 *
 */
public class TestRawIgraphRandomness {

	public TestRawIgraphRandomness() {
		
	}
	
	protected String retrieveVersion() {
	
		PointerByReference str = new PointerByReference();
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference subminor = new IntByReference();
	
		int res = IGraphRawLibrary.igraph_version(str, major, minor, subminor);
		assertEquals("error while retrieving version", 0, res);
		
		Pointer p = str.getValue();
		String versionString = p.getString(0);
		
		System.err.println(versionString);
		
		return versionString ;
		
	}

	@Test
	public void testAccessVersion() {
		
					
		String versionString = retrieveVersion();
		assertNotNull(versionString);
		
	}
	
	@Test
	public void testVersionCompliance() {
		

		String versionString = retrieveVersion();

		assertTrue(
				"this version of igraph was not tested: "+versionString, 
				versionString.equals("0.6.5")
				);
		
		
	}

	
	@Test
	public void testAccessRNGref() {
		
		System.err.println("access ?");
		Pointer p = IGraphRawLibrary.igraph_rng_default();
		
		assertNotNull(p);
		
		String name = IGraphRawLibrary.igraph_rng_name(p);
		assertNotNull(name);
		
		System.err.println(name);
		
	}

		
	protected long getOneRandomInt(Pointer p, long min, long max) {

		NativeLong result = IGraphRawLibrary.igraph_rng_get_integer(
				p, 
				new NativeLong(min), 
				new NativeLong(max)
				);

		assertNotNull(result);
		
		long v = result.longValue();
				
		assertTrue(v >= min);
		assertTrue(v <= max);
		
		return v;
	}
	
	protected Pointer retrieveDefaultRNG() {
		
		Pointer p = IGraphRawLibrary.igraph_rng_default();
		
		int res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(getASeed()));
		assertEquals("error while setting the seed", 0, res);
		
		return p;
	}
	
	protected void testManyRandomInts(long min, long max, int count) {
		
		Pointer p  = retrieveDefaultRNG();
		
		long previousValue = 0;
		int similarConsecutiveValues = 0;
		
		for (int i=0; i<count; i++) {
			
			long v = getOneRandomInt(p, min, max);
			
			assertTrue("does not respects min value: received "+v+" for ["+min+":"+max+"]", v>=min);
			assertTrue("does not respects max value: received "+v+" for ["+min+":"+max+"]", v<=max);
			
			if (i>0) {
				// compare with previous result
				
				if (previousValue==v) {
					similarConsecutiveValues++;
					
					if (similarConsecutiveValues >= 10) // pifomètre
						Assert.fail("received several times the same value");
					
				} else {
					similarConsecutiveValues = 0;
				}
				
			}
			previousValue = v;
		}
		
		
	}
	
	protected void testManyRandomIntsAreDifferent(long min, long max, int count) {
		
		Pointer p = retrieveDefaultRNG();

		long previousValue = 0;
		int similarConsecutiveValues = 0;
		
		for (int i=0; i<count; i++) {
			
			long v = getOneRandomInt(p, min, max);
			
			if (i>0) {
				// compare with previous result
				
				if (previousValue==v) {
					similarConsecutiveValues++;
					
					if (similarConsecutiveValues >= 2)
						Assert.fail("received several times the same value");
				} else {
					similarConsecutiveValues = 0;
				}
				
			}
			previousValue = v;
		}
		
		
	}
	
	
	@Test
	public void testAccessRandomInt() {
		
		Pointer p = retrieveDefaultRNG();
		
		getOneRandomInt(p, 1,10);
	

	}
	
	
	@Test
	public void testAccessSetSeed() {
	
		// test no exception
		
		Pointer p = IGraphRawLibrary.igraph_rng_default();
		
		int res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(getASeed()));
		
		assertEquals("error while setting the seed", 0, res); 
		
	}
	
	@Test
	public void testSetSeed() {
		
		Pointer p = retrieveDefaultRNG();
		
		// two computations with the same seed 
		int res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(42));
		assertEquals("error while setting the seed", 0, res); 

		long v1 = getOneRandomInt(p, 1,10);
		
		res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(42));
		assertEquals("error while setting the seed", 0, res); 

		long v2 = getOneRandomInt(p, 1,10);
		
		assertEquals("two computations with the same seed should lead to the same result", v1, v2);

		// another seed should do something else
		res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(24));
		assertEquals("error while setting the seed", 0, res); 

		long v3 = getOneRandomInt(p, 1,10);
		assertNotEquals("computations with different seeds should probably send different results", v2, v3);
		
		// and first seed back
		res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(42));
		assertEquals("error while setting the seed", 0, res); 

		long v4 = getOneRandomInt(p, 1,10);
		assertNotEquals(v3, v4);
		assertEquals(v2, v4);
		
	}
	
	private void testSeveralTimes(Pointer p, long seed, int count) {

		long val = 0;
		
		for (int i=0; i<count; i++) {
			
			int res = IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(seed));
			assertEquals("error while setting the seed", 0, res); 

			long v = getOneRandomInt(p, 1,10);
			
			if (i == 0)
				val = v;
			else
				assertEquals("same seed should lead to the same value", val, v);
			
		}
		
	}
	
	@Test
	public void testSameSeedMeanSameValue() {
		
		Pointer p = retrieveDefaultRNG();
		
		testSeveralTimes(p, 42, 100);
		for (int i=0; i<10; i++)  {
			testSeveralTimes(p, getASeed(), 100);
		}
		
	}
	
	protected long getASeed() {
		return (long)Math.round(Math.random()*65000);
	}

	@Test
	public void testDifferentSeedMeanDifferentValue() {
		
		
		Pointer p = IGraphRawLibrary.igraph_rng_default();
		
		long previousVal = 0;
		
		for (int i=0; i<50; i++) {
			
			// two computations with the same seed 
			long seed = getASeed();
			IGraphRawLibrary.igraph_rng_seed(p, new NativeLong(seed ));
			long v = getOneRandomInt(p, 1,50000);
			
			if (i != 0)
				assertNotEquals("different seeds should lead to different values (probably)", previousVal, v);
			
			previousVal = v;
		}
		
	}

	

	@Test
	public void testRandomIntsLimits() {
					
		
		testManyRandomInts(0, 100, 100);
		testManyRandomInts(Integer.MAX_VALUE-10, Integer.MAX_VALUE-1, 100);
		testManyRandomInts(Integer.MAX_VALUE-10, Integer.MAX_VALUE, 100);
		// Long is not necessarily supported !
		// testManyRandomInts(Long.MAX_VALUE-10, Long.MAX_VALUE-1, 100);
		testManyRandomInts(Integer.MIN_VALUE+1, 1, 1000);
		testManyRandomInts(Integer.MIN_VALUE, 1, 100);

	}
	
	@Test
	public void testAccessRandomIntsEfficiency() {
		
		long timestampStart = System.currentTimeMillis();
		
		int count = 2000;
		
		testManyRandomInts(0, 65535, count);
		
		long duration = System.currentTimeMillis() - timestampStart;
		
		
		System.err.println("duration: "+duration+" ms, so "+(Math.round((double)duration*1000/(double)count))+"nano second per number");

		// accept 80 nano seconds per random number (average)
		final double THRESHOLD_NANO_SECOND = 80;
		
		assertTrue("efficiency is very bad: "+duration+"ms for "+count+" random numbers", duration < count*THRESHOLD_NANO_SECOND/1000f);
		
		
	}

}
