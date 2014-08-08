package genlab.bayesianInference;

import java.util.Arrays;
import java.util.LinkedList;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractTestNetwork {

	protected IBayesianNetwork network;
	
	public AbstractTestNetwork() {
		// TODO Auto-generated constructor stub
	}
	
	protected abstract IBayesianNetwork createEmptyNetwork();


	@Test
	public void testNetworkCreation() {
		
		IBayesianNetwork net = createEmptyNetwork();
		
	
		assertEquals(0, net.getAllNodesIds().size()); 
		
	}

	@Test
	public void testNodeCreation() {
		
		IBayesianNetwork net = createEmptyNetwork();
		
		// add a node, ensure it exists
		IBayesianNode nodeWeather = net.createNode("weather", Arrays.asList("rainy", "sunny"));
		
		assertEquals(1, net.getAllNodesIds().size()); 
		assertEquals(nodeWeather, net.getForID("weather"));
		assertEquals(2, nodeWeather.getDomainSize());
		assertEquals("rainy", nodeWeather.getDomain().get(0));
		assertEquals("sunny", nodeWeather.getDomain().get(1));
		assertEquals(2, nodeWeather.getCPT().getCardinality());
		
		nodeWeather.getCPT().fill(0.5);
		
		assertEquals(0.5, nodeWeather.getCPT().getProbabilityForCellIdx(0), 0.0001);
		assertEquals(0.5, nodeWeather.getCPT().getProbabilityForCellIdx(1), 0.0001);
		
	}
	

	@Test
	public void testEdgeCreation() {
		
		IBayesianNetwork net = createEmptyNetwork();
		
		// add a node, ensure it exists
		IBayesianNode nodeWeather = net.createNode("weather", Arrays.asList("rainy", "sunny"));
		nodeWeather.getCPT().fill(0.5);
		IBayesianNode nodeRain = net.createNode("rain", Arrays.asList("yes", "no"));
		
		// check before edge creation
		assertEquals(2, nodeWeather.getCPT().getCardinality());
		assertEquals(2, nodeRain.getCPT().getCardinality());
		assertFalse(net.containsEdge(nodeWeather, nodeRain));
		
		// add edge
		net.addEdge(nodeWeather, nodeRain);
		
		// checks after 
		
		assertTrue(net.containsEdge(nodeWeather, nodeRain));
		assertTrue(net.containsEdge("weather", "rain"));
		assertFalse(net.containsEdge(nodeRain, nodeWeather));
		
		assertEquals(2, nodeWeather.getCPT().getCardinality());
		assertEquals(4, nodeRain.getCPT().getCardinality());
		
		assertEquals("weather", net.topologicalOrder().get(0).getID());
		
	}

	protected void compareOriginalNetworkAndReadenOne(IBayesianNetwork net1, IBayesianNetwork net2) {
		
		// not the same object
		assertNotEquals(net1, net2);
		
		// same attributes
		assertEquals(net1.getAllNodes().size(), net2.getAllNodes().size());
				
		// TODO other tests !
		
		// same nodes
		for (String id: net1.getAllNodesIds()) {
			assertTrue(net2.containsNode(id));
			assertEquals(net1.getForID(id).getDomainSize(), net2.getForID(id).getDomainSize());
			assertEquals(net1.getForID(id).getCPT().getCardinality(), net2.getForID(id).getCPT().getCardinality());
		}
		

		// try to add one node to the first
		net1.createNode("test1");
		assertTrue(net1.containsNode("test1"));
		assertFalse(net2.containsNode("test1"));
		
		// cancel this stupid change
		net1.deleteNode("test1");		
		
		
		// TODO try some inference to be sure !
	}

	@Test
	public void testLoadFromFile() {
		
	}

}
