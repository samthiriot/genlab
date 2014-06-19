package genlab.algog.testing;

import org.junit.Test;

public class TestAlgoGNetwork {

	@Test
	public void testAlgoGOnWSwithFailures() {
		(new CaseAlgoGnetwork(
				//Double targetClustering, 
				0.21, 
				//Double targetDensity,
				0.11,
				//Double probaMutationP, 
				0.1,
				// Double probaMutationK,
				0.1, 
				// Double probaMutationN, 
				0.1, 
				// Integer generations, 
				5,
				// Integer popSize,
				20,
				//String destFileAll, 
				"/tmp/nsgaAll.csv",
				//String destFilePareto
				"/tmp/nsgaPareto.csv"
				)).execAll(
				false,
				false,
				false
				);
		
	}



}
