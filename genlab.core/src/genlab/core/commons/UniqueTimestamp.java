package genlab.core.commons;

/**
 * provides tuples (timestamp, id) that are really unique (meaning, 
 * if the timestamp is the same, there is another id that makes it unique)
 * Thread safe.
 * Assumes we will not have more than Integer.MAX_VALUE messages per minimum system timestemp (milli ?).
 * 
 * @author Samuel THiriot
 *
 */
public class UniqueTimestamp implements Comparable<UniqueTimestamp> {

	public final long timestamp;
	public final int id;
	
	protected Object mutexTimestamp = new Object();

	protected static long lastTimestamp = 0;
	protected static int lastId = 0;

	/**
	 * Creates a new instance with current time.
	 */
	public UniqueTimestamp() {
		
		timestamp = System.currentTimeMillis();

		synchronized (mutexTimestamp) {
			
			if (timestamp == lastTimestamp) {
				// several identical timestamps; will add the order information
				id = ++lastId;
			} else {
				// this is a different timestamp; save it
				lastTimestamp = timestamp;
				lastId = 0;
				id = 0;
			}
			
		}
		
	}


	@Override
	public int compareTo(UniqueTimestamp other) {
		// compares based on timestamp or (if equal) on id
		return timestamp > other.timestamp ? 
				+1 : 
					timestamp < other.timestamp ? 
					-1 : 
						id-other.id;

					
	}

	public String toString() {
		return timestamp+"_"+id;
	}
	

}
