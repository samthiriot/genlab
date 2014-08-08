package genlab.bayesianInference;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import genlab.bayesianinference.Activator;
import genlab.bayesianinference.IInferenceEngine;

import java.io.File;
import java.util.Arrays;

public abstract class AbstractTestEvidencePropagation {

	public static final String fileWeather = "../genlab.bayesianInference/tests/genlab/bayesianInference/weather.net";
	
	protected String getFilenameForTestWeather() {
		
		return (new File(fileWeather)).getAbsolutePath();
	}
	
	protected void checkDistribution(IInferenceEngine ie, String attributeName, double[] expectedDistribution) {
		
		int[] index2occurences = new int[expectedDistribution.length];
		
		final int totalTries = 50000;
		
		for (int count=0; count<totalTries; count++) {
			int indexGenerated = ie.getAttributeRandomnlyGiven(attributeName);
			index2occurences[indexGenerated]++;
		}
		
		System.out.println(Arrays.toString(index2occurences));
		
		for (int i=0; i<expectedDistribution.length; i++) {

			assertEquals(
					expectedDistribution[i], 
					(double)index2occurences[i]/(double)totalTries, 
					0.01
					);
		}
		
	}
	
	protected void testInferenceWeather1(IInferenceEngine ie) {
				
		double[] post;
		
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{0.4,0.4,0.2}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{0.5,0.5}, post, 0.000000001);
		
		// assert evidence
		ie.addEvidenceForAttribute("weather", 0);
		
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{1.0,0.0,0.0}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{0.9,0.1}, post, 0.000000001);

		// reset all evidence
		ie.resetEvidence();
		
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{0.4,0.4,0.2}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{0.5,0.5}, post, 0.000000001);
		
		// assert evidence
		ie.addEvidenceForAttribute("weather", 0);
				
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{1.0,0.0,0.0}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{0.9,0.1}, post, 0.000000001);

		// reset this evidence
		ie.retractEvidence("weather");
		
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{0.4,0.4,0.2}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{0.5,0.5}, post, 0.000000001);

		// check generation
		checkDistribution(ie, "rain", new double[]{0.5,0.5});
		checkDistribution(ie, "weather", new double[]{0.4,0.4,0.2});
		
		// assert evidence
		ie.addEvidenceForAttribute("rain", 0);
		
		// check posteriors
		post = ie.getPosteriors("weather");
		assertArrayEquals(new double[]{0.72,0.08,0.2}, post, 0.000000001);
		post = ie.getPosteriors("rain");
		assertArrayEquals(new double[]{1.0,0.0}, post, 0.000000001);
		
		// check generation
		checkDistribution(ie, "rain", new double[]{1.0,0.0});
		checkDistribution(ie, "weather", new double[]{0.72,0.08,0.2});
		
		// display stats
		ie.exportPropagationStatistics(System.out);
		
	}
	
	
}
