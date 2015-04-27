package genlab.quality;

import java.util.HashMap;
import java.util.Map;

import genlab.core.GenLab;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;

import org.eclipse.swt.widgets.Display;

/**
 * 
 * Tests the responsivity of the SWT thread and runs audits to understand who blocks it. 
 * 
 * @author Samuel Thiriot
 *
 */
public final class TestResponsivity extends Thread {

	
	/**
	 * if true, all the users of the SWT thread should report use here (notifySWTThreadUserSubmitsRunnable, notifySWTThreadUserExecutesRunnable, notifySWTThreadUserEndsRunnable).
	 * Audit can be started during execution if the thread appears to be overloaded.
	 */
	public static boolean AUDIT_SWT_THREAD_USE = false;
	
	
	public static final boolean DISPLAY_ALERTS_STDERR = false;
	
	/**
	 * Delay, in ms, where the display is assumed to be unresponsive
	 * No alert will be shown for these X first ms.
	 */
	public static final long STARTUP_GRACE_PERIOD = 10000;

	public static final long PERIOD_TEST = 2000; // ms
	
	public static final int THRESHOLD_ALERT_PEAK = 500; // ms
	
	public static final int THRESHOLD_AUDIT_ALERT= 100; // ms

	
	private Runnable testRunnable;
	
	private long timestampSubmission;
	
	private boolean cancel = false;
	
	private boolean submitted = false;
	
	public static final TestResponsivity singleton = new TestResponsivity();
	
	/**
	 * the runnable to be transmitted to the SWT thread to "ping" it
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	private class PingRunnable implements Runnable {
		
		@Override
		public void run() {
			long timeBeforeRun = System.currentTimeMillis() - timestampSubmission;
			
			if (!AUDIT_SWT_THREAD_USE && (timeBeforeRun >= THRESHOLD_ALERT_PEAK)) {
				if (DISPLAY_ALERTS_STDERR) {
					System.err.println("BAD RESPONSIVITY DETECTED: delay before reaching the SWT thread: "+timeBeforeRun+" ms");
					System.err.println("something is overloading the SWT thread. This is not a normal user experience. Please feel free to open a bug or download a newer version of the soft.");
				}
				ListsOfMessages.getGenlabMessages().warnTech("BAD RESPONSIVITY DETECTED: delay before reaching the SWT thread: "+timeBeforeRun+" ms", getClass());
				ListsOfMessages.getGenlabMessages().infoTech("something is overloading the SWT thread. This is not a normal user experience. Please feel free to open a bug or download a newer version of the soft.", getClass());
				if (!AUDIT_SWT_THREAD_USE) {
					ListsOfMessages.getGenlabMessages().warnTech("starting the audit of usage of the SWT Thread by views...", getClass());
					AUDIT_SWT_THREAD_USE = true;
				}
			}
			submitted = false;
		}
	}
	
	private TestResponsivity() {
		
		
		setName("glTestSWTresponsivity");
		setPriority(MIN_PRIORITY);
		setDaemon(true);
		
		testRunnable = new PingRunnable();
		
	}
	
	public void startResponsivityTest() {
		
		start();

	}
	
	/**
	 * Submits a task to the SWT thread.
	 */
	public void submitTest() {
		submitted = true;
		final Display display = Display.getDefault();
		if (display == null || display.isDisposed()) {
			cancel = true;
			return;
		}
		timestampSubmission = System.currentTimeMillis();
		Display.getDefault().asyncExec(testRunnable);
	}
	
	@Override
	public void run() {
		
		try {
			Thread.sleep(STARTUP_GRACE_PERIOD);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (!cancel) {
			
			if (!submitted)
				submitTest();
			
			try {
				Thread.sleep(PERIOD_TEST);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO test how regular is our submission ? 
		}
	}

	public static void startTestResponsivity() {
		singleton.startResponsivityTest();
	}
	
	protected final Object lockAudit = new Object();
	protected final Map<String, Long> user2submitTask = new HashMap<String, Long>(200);
	protected final Map<String, Long> user2executesTask = new HashMap<String, Long>(200);
	

	protected final Map<String, Long> user2cumulatedTimeThreadAccess = new HashMap<String, Long>(200);
	protected final Map<String, Long> user2cumulatedTimeExecution = new HashMap<String, Long>(200);
	
	public void notifySWTThreadUserSubmitsRunnable(String user) {
		
		//System.out.println("submit: "+user);

		synchronized (lockAudit) {
			//System.out.println("submit2: "+user);

			Long previousValue = user2submitTask.put(user, System.currentTimeMillis());
			
			if (previousValue != null) {
				ListsOfMessages.getGenlabMessages().warnTech("this SWT thread user submitted a task before finishing the previous one: "+user+"; this is prone to interlock and should be avoided", this.getClass());
			}
				
		}
		
	}
	
	private void displayMessageWithException(String msg) {
		
		// create an exception to have a trace.
		RuntimeException e = null;
		try {
			throw new RuntimeException("slow SWT Thread user");
		} catch (RuntimeException e1) {
			e = e1;
		}
		ListsOfMessages.getGenlabMessages().warnTech(msg, this.getClass(), e);
		if (DISPLAY_ALERTS_STDERR) {
			System.err.println(msg);
			e.printStackTrace();
		}
		
	}

	private void displayMessage(String msg) {
		ListsOfMessages.getGenlabMessages().warnTech(msg, this.getClass());
		if (DISPLAY_ALERTS_STDERR) 
			System.err.println(msg);
	}
	
	public void notifySWTThreadUserStartsRunnable(String user) {
		
		//System.out.println("starts: "+user);
		
		synchronized (lockAudit) {

			//System.out.println("starts2: "+user);
			
			final long currentTime = System.currentTimeMillis();
			
			// store in memory
			Long previousValue = user2executesTask.put(user, currentTime);
			
			// detect errors
			if (previousValue != null) {
				displayMessage("this SWT thread user executes a task before finishing the previous one: "+user+"; this is prone to interlock and should be avoided");
			}
			
			// compute stat
			Long submitTime = user2submitTask.get(user);
			if (submitTime == null) {
				displayMessageWithException("this SWT thread user executes a task but did not warned of its submit time: "+user+"; audit is not possible");
				return;
			}
			final long timeToAccessThread = currentTime - submitTime;
			Long previousCumulated = user2cumulatedTimeThreadAccess.get(user);
			long cumulatedTimeAccessThread;
			if (previousCumulated == null) {
				cumulatedTimeAccessThread = timeToAccessThread;
			} else {
				cumulatedTimeAccessThread = timeToAccessThread + previousCumulated;
			}
			user2cumulatedTimeThreadAccess.put(user, cumulatedTimeAccessThread);
				
			// warn if necessary
			if (timeToAccessThread >= THRESHOLD_AUDIT_ALERT) {
				//displayMessage("this SWT thread user waited a long time ("+timeToAccessThread+" ms) before accessing the SWT thread: "+user);
			}
		}
	}
	
	public void notifySWTThreadUserEndsRunnable(String user) {
		
		//System.out.println("ends: "+user);
		
		synchronized (lockAudit) {
			
			//System.out.println("ends2: "+user);

			final long currentTime = System.currentTimeMillis();

			// compute state
			Long execTime = user2executesTask.remove(user);
			if (execTime == null) {
				displayMessageWithException("this SWT thread user ended a task but did not warned of its execution time: "+user+"; audit is not possible");
				return;
			}
			final long timeToExecute = currentTime - execTime;
			Long previousCumulated = user2cumulatedTimeExecution.get(user);
			long cumulatedTimeExecution;
			if (previousCumulated == null) {
				cumulatedTimeExecution = timeToExecute;
			} else {
				cumulatedTimeExecution = timeToExecute + previousCumulated;
			}
			user2cumulatedTimeExecution.put(user, cumulatedTimeExecution);
				
			// clear previous stats
			user2submitTask.remove(user);
			// done before user2executesTask.remove(user);
			
			// warn if necessary
			if (timeToExecute >= THRESHOLD_AUDIT_ALERT) {
				displayMessage("this SWT thread user blocked the SWT Thread for a long time ("+timeToExecute+" ms) and is thus blocking the GUI: "+user);
				
			}
		}
		
	}
	
	
	
}
