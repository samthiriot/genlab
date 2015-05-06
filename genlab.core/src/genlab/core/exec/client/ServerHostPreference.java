package genlab.core.exec.client;

import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;
import java.util.List;

/**
 * Describes a distant server GenLab should connect to.
 * 
 * @author Samuel Thiriot
 *
 */
public class ServerHostPreference {

	public boolean active;
	public String hostname;
	public int port;
	
	public ServerHostPreference(boolean active, String hostname, int port) {
		this.active = active;
		this.hostname = hostname;
		this.port = port;
	}

	public ServerHostPreference(String fromString) {
    	String[] s1;
    	
    	s1 = fromString.split("\\|");
		this.active = Boolean.parseBoolean(s1[0]);
		s1 = s1[1].split(":");
		this.hostname = s1[0];
		this.port = Integer.parseInt(s1[1]);
	
	}
	
	public String toSaveString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Boolean.FALSE.toString());
		sb.append("|");
		sb.append(hostname);
		sb.append(":");
		sb.append(GenlabComputationServer.DEFAULT_PORT);
		return sb.toString();
	}
	
	public static ServerHostPreference[] parseAsArray(String from) {
		
		List<ServerHostPreference> r = parseAsList(from);
		return r.toArray(new ServerHostPreference[r.size()]);
	}
	
	public static List<ServerHostPreference> parseAsList(String from) {
		
		LinkedList<ServerHostPreference> res = new LinkedList<ServerHostPreference>();
		for (String s: from.split("/")) {
			try {
				ServerHostPreference a = new ServerHostPreference(s);
				res.add(a);
			} catch (Exception e) {
				GLLogger.warnUser("unable to load preferences for an host: "+s+"; it will be removed from preferences.", ServerHostPreference.class);
			}
		}

		return res;
	}

	@Override
	public boolean equals(Object obj) {
		
		try {
			ServerHostPreference other = (ServerHostPreference)obj;
			return (other.port == this.port) && (other.hostname.equals(this.hostname));
			
		} catch (ClassCastException e) {
			return false;
		}
		
	}

	@Override
	public int hashCode() {
		return hostname.hashCode() +  port*7;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(hostname).append(":").append(port);
		return sb.toString();
	}
	

}
