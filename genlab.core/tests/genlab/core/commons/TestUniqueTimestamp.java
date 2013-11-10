package genlab.core.commons;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

public class TestUniqueTimestamp {


	@Test(timeout=10)
	public void testCreateOne() {
		
		UniqueTimestamp ut = new UniqueTimestamp();
		
	}

	@Test(timeout=1000)
	public void testUnicityComparableInterface() {
		
		final int countToTest = 100000;
		
		// a treemap will ensure that the comparison by Comparable interface 
		// ensures uniqueness
		SortedMap<UniqueTimestamp, UniqueTimestamp> tm = new TreeMap<UniqueTimestamp, UniqueTimestamp>();
		
		for (int i=0; i<countToTest; i++) {
			UniqueTimestamp ut = new UniqueTimestamp();
			UniqueTimestamp previous = tm.put(ut, ut);
			assertNull(
					"two timestamps are assumed equal by the Comparable interface: "+ut+" and "+previous, 
					previous
					);
		}
		
		assertEquals("more timestampts should have been created", countToTest, tm.size());
		
	}
	
	@Test(timeout=1000)
	public void testUnicityHash() {
		
		final int countToTest = 100000;
		
		// a treemap will ensure that the comparison by Comparable interface 
		// ensures uniqueness
		Set<UniqueTimestamp> hs = new HashSet<UniqueTimestamp>(countToTest);
		
		for (int i=0; i<countToTest; i++) {
			UniqueTimestamp ut = new UniqueTimestamp();
			boolean added = hs.add(ut);
			assertTrue(
					"the timestamp was not added, probably because the hash and equals operations are not unique "+ut, 
					added
					);
		}
		
		assertEquals("more timestampts should have been created", countToTest, hs.size());
		
	}
	
	

}
