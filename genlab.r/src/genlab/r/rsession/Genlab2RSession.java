package genlab.r.rsession;

import genlab.core.usermachineinteraction.GLLogger;

import org.math.R.Rsession;
import org.rosuda.REngine.Rserve.RSession;

public class Genlab2RSession {

	private Genlab2RSession() {
		
	}

	/**
	 * Returns a novel session to be used.
	 * Would be better to finish it at the end, thanks.
	 * @return
	 */
	public static Rsession createNewLocalRSession() {
		return Rsession.newInstanceTry(System.out, null);
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
