package genlab.core.commons;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * provides tuples (timestamp, id) that are really unique (meaning, 
 * if the timestamp is the same, there is another id that makes it unique)
 * Thread safe.
 * Assumes we will not have more than Integer.MAX_VALUE messages per minimum system timestemp (milli ?).
 * 
 * @author Samuel THiriot
 *
 */
public class UniqueTimestamp implements Comparable<UniqueTimestamp>, Externalizable {

	public long timestamp;
	public int id;
	
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


	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		timestamp = in.readLong();
		id = in.readInt();
		
		mutexTimestamp = new Object();
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(timestamp);
		out.writeInt(id);
	}
	

}
