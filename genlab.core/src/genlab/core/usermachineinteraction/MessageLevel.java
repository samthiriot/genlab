package genlab.core.usermachineinteraction;

public enum MessageLevel {

	TRACE,
	DEBUG,
	TIP,
	WARNING,
	INFO,
	ERROR,
	;
	
	public static MessageLevel getLowest() {
		return TRACE;
	}

	public static MessageLevel getHighest() {
		return ERROR;
	}
	
	public static MessageLevel max(MessageLevel l1, MessageLevel l2) {
	
		if (l1.compareTo(l2) >= 0)
			return l1;
		else 
			return l2;
		
	}
	
}
