package genlab.bayesianinference.implementations.inflib;

import static org.junit.Assert.*;

import java.util.Arrays;

import edu.ucla.belief.io.hugin.HuginNetImpl;
import genlab.bayesianInference.AbstractTestEvidencePropagation;
import genlab.bayesianinference.IInferenceEngine;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEvidencePropagation extends AbstractTestEvidencePropagation {


	@Test
	public void testFileLoadExisting() {
		
		HuginNetImpl net = InflibBaysianNetwork.loadFromFile(getFilenameForTestWeather(), false);
		
		// ensure the file is loaded
		assertNotNull("net not loaded", net);
	
		// ensure the nodes do exist
		assertNotNull("node not found", net.forID("weather"));
		assertNotNull("node not found", net.forID("rain"));
		
		// ensure the links are correct
		assertTrue("link not loaded", net.hasPath(net.forID("weather"), net.forID("rain")));
		
		// ensure clearing is not raising errors
		net.clear();
	}

	
	@Test
	public void testInferenceEDPBConditionningCache() {
		
		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new EDPBInflibInferenceEngine(getFilenameForTestWeather()));
		
	}

	@Test
	public void testInferenceWeatherHuginCache() {
		
		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new HuginInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceWeatherLoopyCache() {

		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new LoopyInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceWeatherRecursiveConditionningCache() {

		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new RecursiveConditionningInflibInferenceEngine(getFilenameForTestWeather()));
		
	}

	@Test
	public void testInferenceWeatherShenoyShaferCache() {

		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new ShenoyShaferInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceZCShaferCache() {

		AbstractInflibInferenceEngine.WITH_CACHE = true;

		testInferenceWeather1(new ZCInflibInferenceEngine(getFilenameForTestWeather()));
		
		
	}
	


	@Test
	public void testInferenceEDPBConditionning() {
		
		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new EDPBInflibInferenceEngine(getFilenameForTestWeather()));
		
	}

	@Test
	public void testInferenceWeatherHugin() {
		
		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new HuginInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceWeatherLoopy() {

		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new LoopyInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceWeatherRecursiveConditionning() {

		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new RecursiveConditionningInflibInferenceEngine(getFilenameForTestWeather()));
		
	}

	@Test
	public void testInferenceWeatherShenoyShafer() {

		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new ShenoyShaferInflibInferenceEngine(getFilenameForTestWeather()));
		
	}
	

	@Test
	public void testInferenceZCShafer() {

		AbstractInflibInferenceEngine.WITH_CACHE = false;

		testInferenceWeather1(new ZCInflibInferenceEngine(getFilenameForTestWeather()));
		
		
	}
	

	
	
}
