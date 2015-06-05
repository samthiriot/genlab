package genlab.netlogo;

import java.util.HashSet;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;



// TODO test parallel
// TODO test impact of random seed

public abstract class TestModelBehaviour {

	
	protected abstract Map<String,Object> runModel();
	
	protected abstract void checkResult(Map<String,Object> result);
	
	@Test
	public void testModelBasicRun() {
		
		Map<String,Object> result = runModel();
		checkResult(result);
		
	}
	
	
	private Set<Thread> collectCurrentThreads() {
		Thread[] activeThreadsT = new Thread[Thread.activeCount()];
		Thread.enumerate(activeThreadsT);
		Set<Thread> res = new HashSet<Thread>();
		for (Thread t: activeThreadsT)
			res.add(t);
		return res;
	}
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
			runModel();
		
		// take reference
		final int activeThreads = Thread.activeCount();
		final Set<Thread> activeThreadsSet = collectCurrentThreads();
		
		// run many 
		for (int i=0; i<TEST_COUNT; i++) {
			System.err.println("exec "+i+" threads "+Thread.activeCount());
			runModel();
		}
		
		if (Thread.activeCount() > activeThreads) {
			double nbPerIteration = Thread.activeCount()/TEST_COUNT; 
			Set<Thread> threadsNow = collectCurrentThreads();
			threadsNow.removeAll(activeThreadsSet);
			for (Thread t: threadsNow) {
				System.err.println("extra thread: "+t);
				t.stop();
			}
			Thread.yield();
			System.out.println("did cleaning help ? "+Thread.activeCount()+" / "+activeThreads);
			threadsNow = collectCurrentThreads();
			threadsNow.removeAll(activeThreadsSet);
			for (Thread t: threadsNow) {
				System.out.println("extra thread: "+t);
			}
			fail("about "+nbPerIteration+" novel threads are created at each run; this cannot be used in production");
		}
		
	}
}
