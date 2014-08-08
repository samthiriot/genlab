package genlab.core.performance;

import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.UserMachineInteractionUtils;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores statistics about the efficiency of a set of tasks for a given task set
 * 
 * @author Samuel Thiriot
 *
 */
public final class TimeMonitor {

	private final String name;
	
	private final Map<String,Map<Object,Long>> key2sender2timestart = new HashMap<String, Map<Object,Long>>();

	private final Map<String,Long> key2timetotal = new HashMap<String, Long>();

	/**
	 * Lock to be used to secure multithreading
	 */
	private final Object globalLock = new Object();
	
	public TimeMonitor(String name) {
		
		this.name = name;
		
	}
	
	protected Map<Object,Long> getOrCreateStartForKey(String key) {
		
		Map<Object,Long> map = key2sender2timestart.get(key);
		
		if (map == null) {
			map = new HashMap<Object, Long>();
			key2sender2timestart.put(key, map);
		}
		
		return map;	
		
	}
	
	

	public void notifyTaskStart(String key, Object sender) {
		
		Long previousStart;
		
		synchronized (globalLock) {

			Map<Object,Long> key2starts = getOrCreateStartForKey(key);
			
			previousStart = key2starts.put(sender, System.currentTimeMillis());
			
		}

		if (previousStart != null) {
			GLLogger.warnTech("the task "+key+" was already started for sender "+sender+"; monitoring will be biased", getClass());
		}
	}
	
	
	
	protected Long getStartTimeForTask(String key, Object sender) {
		
		Map<Object,Long> sender2start = getOrCreateStartForKey(key);
		
		if (sender2start == null)
			throw new ProgramException("unable to get the start time for task "+key+" from "+sender+"; its start was not declared.");
		
		Long result = sender2start.remove(sender);
		// don't keep empty maps
		if (sender2start.isEmpty()) {
			key2sender2timestart.remove(key);	
		}
		
		
		return result;
	}
	
	protected void addTotal(String key, Long value) {
		
		Long previous = key2timetotal.get(key);
		if (previous == null)
			key2timetotal.put(key, value);
		else
			key2timetotal.put(key, value+previous);	
		
	}
	
	public void notifyTaskEnd(String key, Object sender) {
		
		synchronized (globalLock) {
			
			addTotal(
					key, 
					System.currentTimeMillis() - getStartTimeForTask(key, sender)
					);	
		}
		
	}
	
	public String getHumanRepresentation() {
		
		synchronized (globalLock) {
			
			StringBuffer sb = new StringBuffer("\n");
			sb.append("statistics for ").append(name).append(":\n");
			
			for (String key: key2timetotal.keySet()) {
			
				sb.append("\t").append(key).append(":\t").append(UserMachineInteractionUtils.getHumanReadableTimeRepresentation(key2timetotal.get(key))).append("\n");
				
			}
			
			return sb.toString();	
		}
		
	}
	
	public void printToStream(PrintStream ps) {
		ps.println(getHumanRepresentation());
	}
	
	public void printToLog() {
		GLLogger.debugTech(getHumanRepresentation(), getClass());
	}
	
}
