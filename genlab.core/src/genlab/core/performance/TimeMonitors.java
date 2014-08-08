package genlab.core.performance;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for monitoring. Use it to construct time monitors.
 * 
 * @author Samuel Thiriot
 *
 */
public final class TimeMonitors {

	public static final TimeMonitors SINGLETON = new TimeMonitors();

	private Map<String,TimeMonitor> name2tm = new HashMap<String, TimeMonitor>();
	
	private TimeMonitors() {
		
	}
	
	public TimeMonitor getMonitor(String name) {
		synchronized (name2tm) {
			TimeMonitor res = name2tm.get(name);
			if (res == null) {
				res = new TimeMonitor(name);
				name2tm.put(name, res);
			}
			return res;
		}
	}
	
	public void printToStream(PrintStream ps) {
		synchronized (name2tm) {
		
			for (TimeMonitor tm: name2tm.values()) {
				tm.printToStream(ps);
			}
		}
	}
	
	public void printToLog() {
		synchronized (name2tm) {
		
			for (TimeMonitor tm: name2tm.values()) {
				tm.printToLog();
			}
		}
	}
}
