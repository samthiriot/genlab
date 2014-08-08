package genlab.bayesianinference;

import java.io.PrintStream;
import java.util.List;

public interface IInferenceEngine {

	public void initFromFile(String filenameBayesianNetwork);
	
	public IBayesianNetwork getBayesianNetwork();
	
	/**
	 * Removes any evidence
	 */
	public void resetEvidence();

	/**
	 * Adds evidence for an attribute
	 * @param attribute
	 * @param index
	 */
	public void addEvidenceForAttribute(String attributeName, int index);

	/**
	 * Removes evidence for this attribute
	 * @param attribute
	 */
	public void retractEvidence(String attributeName);

	/**
	 * generates a value for a discrete attribute
	 * @param attribute
	 * @param debug
	 * @return the index of the value
	 */
	public int getAttributeRandomnlyGiven(String attributeName);

	/**
	 * get posteriors probabilities, for the evidence currently asserted
	 * @param attribute
	 * @return
	 */
	public double[] getPosteriors(String attributeName);

	/**
	 * Returns the value for this attribute index
	 * @param attribute
	 * @param index
	 * @return
	 */
	public Object getValueOf(String attributeName, int index);

	/**
	 * Returns the list of values for this attribute
	 * @param attributeName
	 * @return
	 */
	public List<String> valuesOf(String attributeName);

	
	/**
	 * Displays, if relevant, the statistics of performance
	 * @param ps
	 */
	public void exportPropagationStatistics(PrintStream ps);
	
	
}
