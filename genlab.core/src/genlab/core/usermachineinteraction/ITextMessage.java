package genlab.core.usermachineinteraction;

import genlab.core.commons.UniqueTimestamp;

import java.util.Date;

/**
 * A message that can be transmitted to the user;
 * corresponds to a line in a log.
 * 
 * @author Samuel Thiriot
 *
 */
public interface ITextMessage extends Comparable<ITextMessage> {

	public MessageLevel getLevel();
	
	public MessageAudience getAudience();
	
	public String getMessage();
	
	public UniqueTimestamp getTimestamp();
	
	public Date getDate();
	
	public void addIncrementCount();
	
	public int getCount();
	
	public Throwable getException();
	
	public Class getEmitter();
	
	public String getShortEmitter();
	
}
