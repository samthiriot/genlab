package genlab.r.rsession;

import java.util.HashSet;
import java.util.Set;

import genlab.core.commons.NullPrintStream;
import genlab.core.usermachineinteraction.GLLogger;

import org.math.R.Rsession;

public class Genlab2RSession {

	private Genlab2RSession() {
		
	}

	/**
	 * Returns a novel session to be used.
	 * Would be better to finish it at the end, thanks.
	 * @return
	 */
	public static Rsession createNewLocalRSession() {
		return Rsession.newInstanceTry( 
				System.out,//new NullPrintStream(),//System.out, 
				null
				);
	}
	
	private static Object poolLocker = new Object();
	private static Set<Rsession> poolAvailable = new HashSet<Rsession>(100);
	private static Set<Rsession> poolRunning = new HashSet<Rsession>(100);

	/**
	 * Closes all the R sessions running before leaving. Should be called at application closure time.
	 */
	public static void closeAllSessions() {
	
		try {
			GLLogger.debugTech("closing all the R connections...", Genlab2RSession.class);
			for (Rsession r: poolAvailable) {
				try {
					r.end();
				} catch (RuntimeException e) {
					GLLogger.warnTech("error while closing a R connection, some resources might not be cleaned.", Genlab2RSession.class, e);
				}
			}
			for (Rsession r: poolRunning) {
				try {
					r.end();
				} catch (RuntimeException e) {
					GLLogger.warnTech("error while closing a R connection, some resources might not be cleaned.", Genlab2RSession.class, e);
				}
			}
		} catch (RuntimeException e) {
			GLLogger.warnTech("error while closing the R connections, some resources might not be cleaned.", Genlab2RSession.class, e);
		}
		
	}
	
	public static Rsession pickOneSessionFromPool() {
		synchronized (poolLocker) {
			Rsession res = null;
			// get or create an available session
			if (poolAvailable.isEmpty()) {
				GLLogger.debugTech("all the "+poolRunning.size()+" R sessions are busy; creating another session in pool", Genlab2RSession.class);
				res = createNewLocalRSession();
			} else {
				res = poolAvailable.iterator().next();
				poolAvailable.remove(res);
			}
			poolRunning.add(res);
			return res;
		}
	}
	public static void returnSessionFromPool(Rsession session) {
		synchronized (poolLocker) {
			poolRunning.remove(session);
			poolAvailable.add(session);
			GLLogger.debugTech("a session was returned to the pool; we now have "+poolAvailable.size()+" available vs. "+poolRunning.size()+" used", Genlab2RSession.class);
		}
	}

	private static Boolean isRAvailable = null;
	
	/**
	 * returns true if R seems available
	 * @return
	 */
	public static boolean isRAvailable() {
		// only try to connect if not already tested
		if (isRAvailable == null) {
			try {
				Rsession session = createNewLocalRSession();
				// wait a bit for it to be connected
				long startStamp = System.currentTimeMillis();
				while (!session.connected) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					if (System.currentTimeMillis() - startStamp > 500) {
						GLLogger.warnTech("R session timeout", Genlab2RSession.class);
						break;
					}
				}
				// store result
				isRAvailable = session.connected;
				// clean this connection and free resources
				session.end();
			} catch (RuntimeException e) {
				isRAvailable = false;
			}
		}
		return isRAvailable;
	}
	
}
