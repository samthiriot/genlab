package genlab.bayesianinference.smile;

import smile.Network;
import genlab.bayesianinference.IBayesianNetwork;
import genlab.core.commons.ProgramException;

public class SmileUtils {

	private SmileUtils() {
		
	}
	
	public static SMILEBayesianNetwork readFromFile(String filename) {
		
		try {
			Network smileNet = new Network();
			smileNet.readFile(filename);
			
			SMILEBayesianNetwork bn = new SMILEBayesianNetwork(smileNet);
			
			return bn;
		} catch (RuntimeException e) {
			throw new ProgramException("unable to read Bayesian network from this file "+filename, e);
		}
	}
	
	public static void writeToFile(SMILEBayesianNetwork network, String filename) {
		
		// TODO many checks !
		network.smileNet.writeFile(filename);
	}

	/**
	 * Returns true if the native library is available
	 * @return
	 */
	public static boolean testNativeLibraryIsAvailable() {
		
		try {
			Network net = new Network();
			return true;
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
}
