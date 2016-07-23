package genlab.gnuplot;

public class GnuplotUtils {

	/**
	 * Converts a String to a valid gnuplot variable name
	 * @param s
	 * @return
	 */
	public static String toGnuplotVariableName(String s) {
		return s.replaceAll("[^0-9a-zA-Z]+", "_");
	}
	
	private GnuplotUtils() {
		
	}

}
