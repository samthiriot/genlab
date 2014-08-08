package genlab.bayesianInference.smile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import genlab.bayesianInference.AbstractTestNetwork;
import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.smile.SMILEBayesianNetwork;
import genlab.bayesianinference.smile.SmileUtils;

public class TestSmileNetworks extends AbstractTestNetwork {

	public TestSmileNetworks() {
		
	}

	@Override
	protected IBayesianNetwork createEmptyNetwork() {
		return new SMILEBayesianNetwork();
	}


	protected void testWriteToFile(String filename) {
		
		SMILEBayesianNetwork net = new SMILEBayesianNetwork();
		
		// add a node, ensure it exists
		IBayesianNode nodeWeather = net.createNode("weather", Arrays.asList("rainy", "sunny"));
		nodeWeather.getCPT().fill(0.5);
		IBayesianNode nodeRain = net.createNode("rain", Arrays.asList("yes", "no"));
		
		// add edge
		net.addEdge(nodeWeather, nodeRain);
		
		nodeRain.getCPT().fill(0.5);
		
		// write it
		SmileUtils.writeToFile(net, filename);
		
		// try to read it again
		SMILEBayesianNetwork net2 = SmileUtils.readFromFile(filename);
		
		compareOriginalNetworkAndReadenOne(net, net2);
		
	}
	
	protected String getTmpFilename(String extension) {
	
		try {
			File f = File.createTempFile("junit_genlab_smile", extension);
			return f.getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
	}
	
	@Test
	public void testWriteToFileAsXDSL() {
		testWriteToFile(getTmpFilename(".xdsl"));		
	}

	@Test
	public void testWriteToFileAsDSL() {

		testWriteToFile(getTmpFilename(".dsl"));
	}

	@Test
	public void testWriteToFileAsErgo() {

		testWriteToFile(getTmpFilename(".erg"));
	}

	@Test
	public void testWriteToFileAsNetica() {

		testWriteToFile(getTmpFilename(".dne"));
	}

	@Test
	public void testWriteToFileAsMSBN() {

		testWriteToFile(getTmpFilename(".dsc"));
	}

	@Test
	public void testWriteToFileAsHugin() {

		testWriteToFile(getTmpFilename(".net"));
	}

	/*
	 * Not tested, as this export is more for graphic than semantic elements.
	@Test
	public void testWriteToFileAsKI() {

		testWriteToFile("/tmp/net.dxp");
	}*/


}
