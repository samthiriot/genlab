package genlab.core.usermachineinteraction;

import java.util.Date;

public class TextMessage implements ITextMessage {

	protected final MessageLevel level;
	protected final MessageAudience audience;
	protected final String message;
	protected final Long timestamp;
	protected int count = 1;
	protected final Exception exception;
	
	public TextMessage(MessageLevel level, MessageAudience audience, String message, Exception exception) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = exception;
	}
	
	public TextMessage(MessageLevel level, MessageAudience audience, String message) {
		this.level = level;
		this.audience = audience;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.exception = null;
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
	
	public Exception getException() {
		return exception;
	}

}
