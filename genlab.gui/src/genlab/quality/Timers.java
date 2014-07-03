package genlab.quality;

import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.HashMap;
import java.util.Map;

/**
 * Publishes easy timers to find the places with delay.
 * 
 * @author Samuel Thiriot
 *
 */
public final class Timers {

	public static Timers SINGLETON = new Timers();
	
	private final Map<String,Long> key2timestampStart = new HashMap<String, Long>();
	
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	private Timers() {
	}

	/**
	 * Declares a task was started. If another with the same
	 * key was declared, it will be overriden and a warn message
	 * will be displayed.
	 * 
	 * @param key
	 */
	public void startTask(String key) {
		Long previous = null;
		synchronized (key2timestampStart) {
			previous = key2timestampStart.put(key, System.currentTimeMillis());	
		}
		
		if (previous != null)
			messages.warnTech("a timer was already defined for this task: "+key, getClass());
	}
	
	/**
	 * Declares a task ended. Will report the delay on message 
	 * and transmit it as well.
	 * @param key
	 */
	public long endTask(String key, long displayThreshold) {
		Long start = null;
		synchronized (key2timestampStart) {
			start = key2timestampStart.remove(key);
		}
		if (start == null) {
			messages.warnTech("a timer was finished but not started: "+key, getClass());
			return -1;
		}
		final long duration = (System.currentTimeMillis()-start);
		if (duration >= displayThreshold)
			messages.infoTech("timing for task "+key+": "+duration+" ms", getClass());
		return duration;
	}
	
	public long endTask(String key) {
		return this.endTask(key, 0);
	}
	
}
