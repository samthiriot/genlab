package genlab.core.usermachineinteraction;

import java.util.Date;

public class TextMessage implements ITextMessage {

	public final MessageLevel level;
	public final MessageAudience audience;
	public final String message;
	public final Long timestamp;
	protected int count = 1;
	public final Exception exception;
	public final String fromShort;
	public final Class emitter;
	
	
	public TextMessage(MessageLevel level, MessageAudience audience, String fromShort, Class emitter, String message, Exception exception) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = exception;
		this.fromShort = fromShort;
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, String fromShort, Class emitter, String message) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = null;
		this.fromShort = fromShort;
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, Class emitter, String message, Exception exception) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = exception;
		this.fromShort = emitter.getSimpleName();
		this.emitter = emitter;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, Class emitter, String message) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = null;
		this.fromShort = emitter.getSimpleName();
		this.emitter = emitter;
	}

	@Override
	public MessageLevel getLevel() {
		return level;
	}

	@Override
	public MessageAudience getAudience() {
		return audience;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Long getTimestamp() {
		return timestamp;
	}

	@Override
	public void addIncrementCount() {
		this.count ++;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public int compareTo(ITextMessage arg0) {
		return timestamp.compareTo(arg0.getTimestamp());
	}

	@Override
	public Date getDate() {
		return new Date(timestamp);
	}
	
	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public Class getEmitter() {
		return emitter;
	}

	@Override
	public String getShortEmitter() {
		return fromShort;
	}

}
