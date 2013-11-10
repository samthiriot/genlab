package genlab.core.usermachineinteraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

public class TestTextMessages {

	

	@Test(timeout=10)
	public void testCreateOne() {
		
		TextMessage tm = new TextMessage(
				MessageLevel.DEBUG, 
				MessageAudience.DEVELOPER, 
				getClass(), 
				"truc message"
				);
		
		
	}

	@Test(timeout=1000)
	public void testUnicityComparableInterface() {
		
		final int countToTest = 100000;
		
		// a treemap will ensure that the comparison by Comparable interface 
		// ensures uniqueness
		SortedSet<ITextMessage> tm = new TreeSet<ITextMessage>();
		
		for (int i=0; i<countToTest; i++) {
			TextMessage ut = new TextMessage(
					MessageLevel.DEBUG, 
					MessageAudience.DEVELOPER, 
					getClass(), 
					"test message "+i
					);
			boolean added = tm.add(ut);
			assertTrue(
					"the message was not added, probably because the cimpate operation is not ensuring unicity "+ut, 
					added
					);
			
		}
		
		assertEquals("more messages should have been created", countToTest, tm.size());
		
	}
	
	@Test(timeout=1000)
	public void testUnicityHash() {
		
		final int countToTest = 100000;
		
		// a treemap will ensure that the comparison by Comparable interface 
		// ensures uniqueness
		Set<ITextMessage> hs = new HashSet<ITextMessage>(countToTest);
		
		for (int i=0; i<countToTest; i++) {
			TextMessage ut = new TextMessage(
					MessageLevel.DEBUG, 
					MessageAudience.DEVELOPER, 
					getClass(), 
					"test message "+i
					);
			boolean added = hs.add(ut);
			assertTrue(
					"the message was not added, probably because the hash and equals operations are not unique "+ut, 
					added
					);
		}
		
		assertEquals("more messages should have been created", countToTest, hs.size());
		
	}
}
