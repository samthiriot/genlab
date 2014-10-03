package genlab.bayesianinference.ui.bnj;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ksu.cis.bnj.ver3.core.BeliefNetwork;

public class TestFilePersistence {

	public static final String FILE_BN_WEATHER = "tests/genlab/bayesianinference/ui/bnj/weather.net";
	
	@Test
	public void testLoadNetworkWeatherInBNJFormat() {
		
		BeliefNetwork bn = Genlab2BNJ.loadBBNGraphFromFile(FILE_BN_WEATHER);
		
		assertNotNull(bn);
		assertNotNull(bn.findNode("weather"));
		assertNotNull(bn.findNode("rain"));
		
	}

}
