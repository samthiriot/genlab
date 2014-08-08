package genlab.bayesianInference.smile;

import genlab.bayesianInference.AbstractTestEvidencePropagation;
import genlab.bayesianinference.smile.SMILEInferenceEngine;
import genlab.bayesianinference.smile.SmileUtils;

import org.junit.Test;

public class TestInference extends AbstractTestEvidencePropagation {


	@Test
	public void testInferenceWeather() {

		testInferenceWeather1(
				new SMILEInferenceEngine(
						SmileUtils.readFromFile(getFilenameForTestWeather())
						)
				);
		
	}

}
