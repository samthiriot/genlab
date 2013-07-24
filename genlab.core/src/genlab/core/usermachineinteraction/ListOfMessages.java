package genlab.core.usermachineinteraction;

import genlab.core.commons.WrongParametersException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Stores a set of messages. Orders them by increasing timestamp.
 * Avoids to add many times a message, by just increasing the amount of occurences (for the X last messages).
 * 
 * TODO thread safety, please !
 * TODO observer pattern
 * 
 * @author Samuel Thiriot
 *
 */
public class ListOfMessages implements Iterable<ITextMessage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Quick access to messages by a hash
	 */
	private HashSet<ITextMessage> hashedMessages = new HashSet<ITextMessage>();
	
	/**
	 * All the messages in natural order (by timestamp)
	 */
	private TreeSet<ITextMessage> sortedMessages = new TreeSet<ITextMessage>();

	private LinkedList<IListOfMessagesListener> listeners = new LinkedList<IListOfMessagesListener>();
	
	/**
	 * If true, relays every message to a log4j logger.
	 */
	public static boolean RELAY_TO_LOG4J = true;
	
	protected MessageLevel filterIgnoreBelow = MessageLevel.DEBUG;
	
	
	protected static final Map<MessageLevel,Priority> messageLevel2log4jPriority = new HashMap<MessageLevel, Priority>(){{
		put(MessageLevel.TRACE, Priority.DEBUG);
		put(MessageLevel.DEBUG, Priority.DEBUG);
		put(MessageLevel.INFO, Priority.INFO);
		put(MessageLevel.TIP, Priority.INFO);
		put(MessageLevel.WARNING, Priority.WARN);
		put(MessageLevel.ERROR, Priority.ERROR);
	}};
	
	static {
		// init of LOG4J
		if (RELAY_TO_LOG4J) {
		    BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.DEBUG);
			//Logger.getRootLogger().addAppender(new ConsoleAppender());
		}
	}
	
	public boolean isEmpty() {
		return hashedMessages.isEmpty();
	}
	
	public ITextMessage last() {
		return sortedMessages.last();
	}
	
	/**
	 * Adds without raising event
	 * @param e
	 * @return
	 */
	private void _add(ITextMessage e) {
		
		synchronized (hashedMessages) {

			Iterator<ITextMessage> it = sortedMessages.descendingIterator();
			
			// attempt to find a similar message in time (in the previous 3 messages ?)
			ITextMessage messageIdentical = null;
			{
				ITextMessage current = null;
				int i=3;
				while (it.hasNext() && (i-- > 0) ) {
					
					current = it.next();
					
					if (current.equals(e)) {
						messageIdentical = current;
						break;
					}
						
				}
			}
								
			if (messageIdentical != null) {
				// if the message was already stored just a short time before, just add it.
				messageIdentical.addIncrementCount();
			} else {
				hashedMessages.add(e);
				sortedMessages.add(e);	
				
				if (RELAY_TO_LOG4J) {
					
					final StringBuffer msg = new StringBuffer();
					
					// add the basic message
					msg.append(e.getDate().toString())
					.append(" - ")
					.append(e.getMessage());
					
					// send it using Logger.
					Logger.getLogger((e.getClass()==null?Object.class:e.getEmitter())).log(
							messageLevel2log4jPriority.get(e.getLevel()),
							msg.toString(),
							e.getException()
							);
				}
				
			}
			
		}
		
	}
	
	public boolean add(ITextMessage e) {

		if (!e.getLevel().shouldDisplay(filterIgnoreBelow))
			return false;
		
		_add(e);
		
		for (IListOfMessagesListener l : new LinkedList<IListOfMessagesListener>(getListeners())) {
			l.messageAdded(this, e);
		}
		
		for (IListOfMessagesListener l : new LinkedList<IListOfMessagesListener>(getListeners())) {
			l.contentChanged(this);
		}
		
		return true;
		
	}
	
	
	/**
	 * Returns an iterator on the natural order (that is, sorted by timestamp)
	 * @return
	 */
	public Iterator<ITextMessage> iterator() {
		return sortedMessages.iterator();
	}

	/**
	 * Adds all the messages from the other
	 * @param others
	 * @return
	 */
	public boolean addAll(Iterable<ITextMessage> others) {

		// add messages
		synchronized (hashedMessages) {

			Iterator<ITextMessage> itOther = others.iterator();
			
			while (itOther.hasNext()) {
				ITextMessage m = itOther.next();
				
				if (m.getLevel().shouldDisplay(filterIgnoreBelow))
					this._add(m);
			}
			

		}
		
		// raise messages
		synchronized (listeners) {
			for (IListOfMessagesListener l : getListeners()) {
				l.contentChanged(this);
			}
		}
		
		return true;
		
	}
	
	public void clear() {
		
		synchronized (hashedMessages) {

			hashedMessages.clear();
			sortedMessages.clear();
			
		}
		synchronized (listeners) {
			for (IListOfMessagesListener l : getListeners()) {
				l.contentChanged(this);
			}
		}
		
	}

	public ITextMessage[] asArray() {
		
		synchronized (hashedMessages) {

		return hashedMessages.toArray(new ITextMessage[hashedMessages.size()]);
		
		}
	}
	
	private final Collection<IListOfMessagesListener> getOrCreateListeners() {
		
		return listeners;
	}
	
	public Collection<IListOfMessagesListener> getListeners() {
		return listeners;
	}
	
	public void addListener(IListOfMessagesListener l) {
		if (l == null) 
			throw new WrongParametersException("listeners should never be null");
			
		synchronized (listeners) {
			getOrCreateListeners().add(l);
		}
		
	}
	
	public void removeListener(IListOfMessagesListener l) {
		synchronized (listeners) {
			getOrCreateListeners().remove(l);
		}
	}
	
	public int getSize() {
		synchronized (hashedMessages) {

			return hashedMessages.size();
		}
	}
	
	public void debugUser(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.USER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void debugUser(String message, Class fromClass) {
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.USER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void warnUser(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.USER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void warnUser(String message, Class fromClass) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.USER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void infoUser(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.INFO.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.USER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void infoUser(String message, Class fromClass) {
		if (!MessageLevel.INFO.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.USER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void tipUser(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.TIP.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TIP, 
						MessageAudience.USER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void tipUser(String message, Class fromClass) {
		if (!MessageLevel.TIP.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TIP, 
						MessageAudience.USER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	

	public void errorUser(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.USER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void errorUser(String message, Class fromClass, Throwable e) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.USER, 
						fromClass,
						message, 
						e
						)
			);
	}
	
	public void errorUser(String message, Class fromClass) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.USER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	public void traceTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.TRACE.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TRACE, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void traceTech(String message, Class fromClass) {
		if (!MessageLevel.TRACE.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TRACE, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void traceTech(String message, String fromShort, Class fromClass, Throwable e) {
		if (!MessageLevel.TRACE.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TRACE, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message,
						e
						)
			);
	}
	
	public void traceTech(String message, Class fromClass, Throwable e) {
		if (!MessageLevel.TRACE.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TRACE, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message,
						e
						)
			);
	}
	
	public void debugTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void debugTech(String message, Class fromClass) {
		
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void debugTech(String message, String fromShort, Class fromClass, Throwable e) {
		
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message,
						e
						)
			);
	}
	
	public void debugTech(String message, Class fromClass, Throwable e) {
		if (!MessageLevel.DEBUG.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message,
						e
						)
			);
	}
	
	public void warnTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void warnTech(String message, Class fromClass) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void warnTech(String message, String fromShort, Class fromClass, Throwable e) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message,
						e
						)
			);
	}
	
	public void warnTech(String message, Class fromClass, Throwable e) {
		if (!MessageLevel.WARNING.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message,
						e
						)
			);
	}
	
	public void infoTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.INFO.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void infoTech(String message, Class fromClass) {
		if (!MessageLevel.INFO.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void tipTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.TIP.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TIP, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void tipTech(String message, Class fromClass) {
		if (!MessageLevel.TIP.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.TIP, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void errorTech(String message, String fromShort, Class fromClass) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message
						)
			);
	}
	
	public void errorTech(String message, Class fromClass) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message
						)
			);
	}
	
	public void errorTech(String message, String fromShort, Class fromClass, Throwable e) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						fromShort,
						fromClass,
						message,
						e
						)
			);
	}
	
	public void errorTech(String message, Class fromClass, Throwable e) {
		if (!MessageLevel.ERROR.shouldDisplay(filterIgnoreBelow))
			return;
		add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						fromClass.getSimpleName(),
						fromClass,
						message,
						e
						)
			);
	}
}

