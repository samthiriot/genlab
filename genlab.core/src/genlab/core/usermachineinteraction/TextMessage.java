package genlab.core.usermachineinteraction;

import genlab.core.commons.UniqueTimestamp;

import java.util.Date;

public class TextMessage implements ITextMessage {

	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public final MessageLevel level;
	public final MessageAudience audience;
	public final String message;
	public final UniqueTimestamp timestamp;
	protected int count = 1;
	public final Throwable exception;
	public final String fromShort;
	
	
	@SuppressWarnings("rawtypes")
	public final Class emitter;
	
	public TextMessage(MessageLevel level, MessageAudience audience, String fromShort, Class emitter, String message, Throwable exception) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = new UniqueTimestamp();
		this.exception = exception;
		this.fromShort = fromShort;
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, String fromShort, Class emitter, String message) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = new UniqueTimestamp();
		this.exception = null;
		this.fromShort = fromShort;
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, Class emitter, String message, Throwable exception) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = new UniqueTimestamp();
		this.exception = exception;
		this.fromShort = emitter.getSimpleName();
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, Class emitter, String message) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = new UniqueTimestamp();
		this.exception = null;
		this.fromShort = emitter.getSimpleName();
		this.emitter = emitter;
	}

	@Override
	public final MessageLevel getLevel() {
		return level;
	}

	@Override
	public final MessageAudience getAudience() {
		return audience;
	}

	@Override
	public final String getMessage() {
		return message;
	}
	
	@Override
	public final String getMessageFirstLine() {
		int idx = message.indexOf(LINE_SEPARATOR);
		if (idx > 0)
			return message.substring(0, idx)+" [...]";
		else 
			return message;
	}

	@Override
	public final UniqueTimestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public final void addIncrementCount() {
		this.count ++;
	}

	@Override
	public final int getCount() {
		return count;
	}

	@Override
	public final int compareTo(ITextMessage arg0) {
		return timestamp.compareTo(arg0.getTimestamp());
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		try {
			TextMessage t = (TextMessage)other;
			return 	(this.audience == t.audience) &&
					(this.level == t.level) && 
					(this.emitter == t.emitter) &&
					(this.message.equals(t.message));
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public final Date getDate() {
		return new Date(timestamp.timestamp);
	}
	
	@Override
	public final Throwable getException() {
		return exception;
	}

	@Override
	public final Class getEmitter() {
		return emitter;
	}

	@Override
	public final String getShortEmitter() {
		return fromShort;
	}
	
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(timestamp.timestamp).append(" |\t");
		sb.append(level).append(" |\t");
		sb.append(audience).append(" |\t");
		if (count > 1) {
			sb.append("(").append(count).append(" times) ");
		}
		sb.append(message);
		return sb.toString();
	}


}
