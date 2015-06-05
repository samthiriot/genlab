import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;


/**
 * Ensures that a task is not creating novel threads, or at least 
 * not one per run.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractTestThreadsCreation {

	
	protected abstract void runTaskToCheck();
	

	/**
	 * Runs the same model many times and ensures it is not creating many threads which 
	 * would finish blocking the JVM
	 */
	@Test
	public void testModelUsageOfThreads() {
		
		// first runs after which the reference will be taken
		final int HEATING_COUNT = 2;
		final int TEST_COUNT = 10;
		
		// heading period
		for (int i=0; i<HEATING_COUNT; i++)  
			runTaskToCheck();
		
		// take reference
		final int activeThreads = Thread.activeCount();
		
		// run many 
		for (int i=0; i<TEST_COUNT; i++) {
			System.err.println("exec "+i+" threads "+Thread.activeCount());
			runTaskToCheck();
		}
		
		double nbPerIteration = (Thread.activeCount()-activeThreads)/TEST_COUNT; 
		if (nbPerIteration >= 1.0) {
			fail("about "+nbPerIteration+" novel threads are created at each run; this cannot be used in production");
		}
		
	}
}
