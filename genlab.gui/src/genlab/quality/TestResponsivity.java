package genlab.quality;

import genlab.core.GenLab;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;

import org.eclipse.swt.widgets.Display;

/**
 * 
 * Tests the responsivity of the SWT thread.
 * 
 * @author Samuel Thiriot
 *
 */
public class TestResponsivity extends Thread {

	public static final boolean DISPLAY_ALERTS_STDERR = true;
	
	/**
	 * Delay, in ms, where the display is assumed to be unresponsive
	 * No alert will be shown for these X first ms.
	 */
	public static final long STARTUP_GRACE_PERIOD = 1000;

	public static final long PERIOD_TEST = 2000; // ms
	
	public static final int THRESHOLD_ALERT_PEAK = 200; // ms
	
	private Runnable testRunnable;
	
	private long timestampSubmission;
	
	private boolean cancel = false;
	
	private boolean submitted = false;
	
	public TestResponsivity() {
		
		testRunnable = new Runnable() {
			
			@Override
			public void run() {
				long timeBeforeRun = System.currentTimeMillis() - timestampSubmission;
				
				if (DISPLAY_ALERTS_STDERR && (timeBeforeRun >= THRESHOLD_ALERT_PEAK)) {
					System.err.println("BAD RESPONSIVITY DETECTED: delay before reaching the SWT thread: "+timeBeforeRun+" ms");
					System.err.println("something is overloading the SWT thread. This is not a normal user experience. Please feel free to open a bug or download a newer version of the soft.");
					ListsOfMessages.getGenlabMessages().warnTech("BAD RESPONSIVITY DETECTED: delay before reaching the SWT thread: "+timeBeforeRun+" ms", getClass());
					ListsOfMessages.getGenlabMessages().infoTech("something is overloading the SWT thread. This is not a normal user experience. Please feel free to open a bug or download a newer version of the soft.", getClass());
				}
				submitted = false;
			}
		};
		
		setName("glTestSWTresponsivity");
		setPriority(MIN_PRIORITY);
		setDaemon(true);
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
		new TestResponsivity();
	}
}
