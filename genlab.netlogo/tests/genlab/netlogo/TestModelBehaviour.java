package genlab.netlogo;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;

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
	
	@Test
	public void testModelSuccessiveRuns() {
		
		for (int i=0; i<20; i++) {
			Map<String,Object> result = runModel();
			checkResult(result);
		}
		
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
	

	/**
	 * Runs the same model many times in parallel and ensures it is still giving relevant results 
	 * would finish blocking the JVM
	 */
	@Test
	public void testModelParallel() {
		
		ListsOfMessages.getGenlabMessages().setFilterIgnoreBelow(MessageLevel.TRACE, MessageLevel.TRACE);
		
		int countThreadBeginning = Thread.activeCount();
		
		final int PARALLELL_THREADS = 10;

		class ThreadRunModel extends Thread {
			
			private final Set<ThreadRunModel> threadsToWait;
			
			public ThreadRunModel(Set<ThreadRunModel> threadsToWait) {
				setDaemon(false);
				this.threadsToWait = threadsToWait;
			}
			@Override
			public void run() {
				
				for (int i=0; i<30; i++) {
					System.err.println("wait");
					// wait a bit randomly
					try {
						Thread.sleep((long)(2000*Math.random()));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// run the model
					System.err.println("run !");
					Map<String,Object> result = runModel();
					checkResult(result);
					
				}
				// return 
				synchronized (threadsToWait) {
					this.threadsToWait.remove(this);	
				}
			}
			
		}
		
		final Set<ThreadRunModel> threadsToWait = new HashSet<ThreadRunModel>();
		
		
		// create threads
		for (int i=0; i<PARALLELL_THREADS; i++) {
			threadsToWait.add(new ThreadRunModel(threadsToWait));
		}
		
		// start threads
		for (ThreadRunModel t: threadsToWait) {
			t.start();
		}
		
		// wait for all of them to finish
		while (true) {
			synchronized (threadsToWait) {
				if (threadsToWait.isEmpty())
					break;
				System.out.println("waiting for "+threadsToWait.size()+" model threads");
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("thread count: before "+countThreadBeginning+", after "+Thread.activeCount());
	}
}
