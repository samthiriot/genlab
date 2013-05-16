package genlab.core.usermachineinteraction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Stores a set of messages. Orders them by increasing timestamp.
 * Avoids to add many times a message, by just increasing the amount of occurences.
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

	private LinkedList<IListOfMessagesListener> listeners = null;
	
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
	private boolean _add(ITextMessage e) {

		synchronized (hashedMessages) {

		ITextMessage messageJustBefore = sortedMessages.lower(e);
				
			if (
					messageJustBefore != null 
					&& messageJustBefore.getAudience() == e.getAudience()
					&& messageJustBefore.getLevel() == e.getLevel()
					&& messageJustBefore.getMessage().equals(e.getMessage())
					) {
				// if the message was already stored just a short time before, just add it.
				messageJustBefore.addIncrementCount();
			} else {
				hashedMessages.add(e);
				sortedMessages.add(e);	
			}
			
		}
		return true;
		
	}
	
	public boolean add(ITextMessage e) {

		_add(e);
		
		for (IListOfMessagesListener l : getListeners()) {
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

		synchronized (hashedMessages) {

			Iterator<ITextMessage> itOther = others.iterator();
			
			while (itOther.hasNext()) {
				this._add(itOther.next());
			}
			
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
		
		for (IListOfMessagesListener l : getListeners()) {
			l.contentChanged(this);
		}
	}

	public ITextMessage[] asArray() {
		
		synchronized (hashedMessages) {

		return sortedMessages.toArray(new ITextMessage[sortedMessages.size()]);
		
		}
	}
	
	private final Collection<IListOfMessagesListener> getOrCreateListeners() {
		if (listeners == null)
			listeners = new LinkedList<IListOfMessagesListener>();
		
		return listeners;
	}
	public Collection<IListOfMessagesListener> getListeners() {
		if (listeners == null)
			return Collections.EMPTY_LIST;
		else 
			return listeners;
	}
	
	public void addListener(IListOfMessagesListener l) {
		getOrCreateListeners().add(l);
	}
	
	public void removeListener(IListOfMessagesListener l) {
		getOrCreateListeners().remove(l);
	}
	
	public int getSize() {
		synchronized (hashedMessages) {

			return sortedMessages.size();
		}
	}
	
	public void debugUser(String message, String fromShort, Class fromClass) {
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
	
	public void errorUser(String message, Class fromClass) {
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
	public void debugTech(String message, String fromShort, Class fromClass) {
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
	
	public void debugTech(String message, String fromShort, Class fromClass, Exception e) {
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
	
	public void debugTech(String message, Class fromClass, Exception e) {
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
	
	public void warnTech(String message, String fromShort, Class fromClass, Exception e) {
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
	
	public void warnTech(String message, Class fromClass, Exception e) {
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
	
	public void errorTech(String message, String fromShort, Class fromClass, Exception e) {
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
	
	public void errorTech(String message, Class fromClass, Exception e) {
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
