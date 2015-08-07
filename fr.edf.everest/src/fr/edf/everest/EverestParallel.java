package fr.edf.everest;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.Map;

public class EverestParallel {

	
	private static EverestParallel singleton = null;
	
	private Map<String,Integer> url2countUsed = new HashMap<>(20);
	
	private final Object lockMeToKnowWhenComputationFinished = new Object();
	
	public static EverestParallel getEverestParallel() {
		if (singleton == null)
			singleton = new EverestParallel();
		return singleton;
	}
	
	/**
	 * Call it from a synchronized block
	 * @param urlEverest
	 */
	private void incrementUseServer(String urlEverest) {
		url2countUsed.put(urlEverest, url2countUsed.get(urlEverest)+1);
	}
	
	/**
	 * Call it to wait for an Everest connection. 
	 * Will return immediatly or wait, possibly a long time, for Everest to be available.
	 * @param urlEverest
	 * @param maxparallel
	 */
	public void waitForConnectionToEverestURL(String urlEverest, Integer maxparallel) {
		
		GLLogger.traceTech("asking for an Everest connection to "+urlEverest, getClass());
				
		try {
		Integer countUsed = null;
		
		synchronized (url2countUsed) {
			
			if (!url2countUsed.containsKey(urlEverest)) {
				url2countUsed.put(urlEverest, 0);
			}
		
			countUsed = url2countUsed.get(urlEverest);
			
			// quick exit: there is immediatly a server available \o/
			if (countUsed < maxparallel) {
				incrementUseServer(urlEverest);
				GLLogger.traceTech("Everest available with no delay for "+urlEverest+", it is used by "+countUsed, getClass());
				return;
			}
		}
			
		// wait for a slot until one is free
		GLLogger.traceTech("waiting for an Everest slot: "+countUsed+" over "+maxparallel+" for "+urlEverest, getClass());

		while (true) {
			
			// we already have to many people working there; lets wait !
			try {
				synchronized (lockMeToKnowWhenComputationFinished) {
					lockMeToKnowWhenComputationFinished.wait();	
				}
				GLLogger.traceTech("unlocked", getClass());
			} catch (InterruptedException e) {
				// who cares about being interupted in our world ?
			}
			
			// check again !
			synchronized (url2countUsed) {
				
				countUsed = url2countUsed.get(urlEverest);
				
				// quick exit: there is immediatly a server available \o/
				if (countUsed < maxparallel) {
					incrementUseServer(urlEverest);
					GLLogger.traceTech("Everest now available for "+urlEverest+", it is used by "+countUsed, getClass());
					return;
				}
			}
			
			GLLogger.traceTech("still waiting for an Everest slot: "+countUsed+" over "+maxparallel+" for "+urlEverest, getClass());
		}
				
		} finally {
			
			if (url2countUsed.get(urlEverest) >= 1) {
				// add a small delay to shift the load of the Everest server to different steps of simulations
				long timesleep = Math.round(Math.random()*10000)+1000;
				GLLogger.traceTech("sleeping during "+timesleep+"ms to reduce the server load", getClass());
				try {
					Thread.sleep(timesleep);
				} catch (InterruptedException e) {
					// we don't care if people don't let us sleep.
				}
			}
		}
	}
	
	/**
	 * Indicate your release an Everest connection
	 * @param urlEverest
	 * @param maxparallel
	 */
	public void freeEverestURL(String urlEverest) {
		
		GLLogger.traceTech("freeing an Everest connection to "+urlEverest, getClass());

		// update the countdown
		synchronized (url2countUsed) {
			Integer newCountDown = Math.max(0, url2countUsed.get(urlEverest)-1);
			url2countUsed.put(urlEverest, newCountDown);
			GLLogger.traceTech("freeing an Everest connection, there are now "+newCountDown+" working for "+urlEverest, getClass());
		}

		synchronized (lockMeToKnowWhenComputationFinished) {
			// wake up one of the threads which are waiting for a slot
			lockMeToKnowWhenComputationFinished.notify();
		}
		
	}

	private EverestParallel() {
	
	}

}
