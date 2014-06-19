package genlab.testing.referenceWorkflows;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.testing.commons.BasicTestWorkflow;

import org.junit.Test;

public class TestSimpleGraphGeneratorChains {

	/**
	 * For tests of WS
	 * 
	 */
	protected class BasicTestWorkflowWS extends BasicTestWorkflow {

		final int N;
		final int K;
		final double p;
		
		public BasicTestWorkflowWS(int N, int K, double p) {
			this.N = N;
			this.K = K;
			this.p = p;
		}
		
		@Override
		protected void populateWorkflow(IGenlabWorkflowInstance workflow) {
			
			// ref algos
			WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
			ConstantValueInteger constantInt = new ConstantValueInteger();
			ConstantValueDouble constantDouble = new ConstantValueDouble();
			StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
			
			// create instances inside the workflow
			{	
				IAlgoInstance wsInstance = ws.createInstance(workflow);
				workflow.addAlgoInstance(wsInstance);
				
				{
					IAlgoInstance constantN = constantInt.createInstance(workflow);
					workflow.addAlgoInstance(constantN);
					constantN.setValueForParameter(constantInt.getConstantParameter(), this.N);
					workflow.connect(
							 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
							 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
					);
				}
				
				{
					IAlgoInstance constantK = constantInt.createInstance(workflow);
					workflow.addAlgoInstance(constantK);
					constantK.setValueForParameter(constantInt.getConstantParameter(), this.K);
					workflow.connect(
							constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
							 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
					);
				}
				
				{
					IAlgoInstance constantP = constantDouble.createInstance(workflow);
					workflow.addAlgoInstance(constantP);
					constantP.setValueForParameter(constantDouble.getConstantParameter(), this.p);
					workflow.connect(
							constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
							 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
					);
				}
				
				IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
				workflow.addAlgoInstance(stdOutInstance);
				workflow.connect(
						wsInstance.getOutputInstanceForOutput(WattsStrogatzAlgo.OUTPUT_GRAPH), 
						stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
						);
				
			}
		}
		
	}
	
	@Test
	public void testWorkflowWSOK() {
		
		(new BasicTestWorkflowWS(500, 4, 0.1)).execAll(
				false,
				false,
				false
				);
		
	}
	

	@Test
	/**
	 * Exepected to fail at exec: wrong parameter k
	 */
	public void testWorkflowWSwrongK() {
		
		(new BasicTestWorkflowWS(500, 3, 0.1)).execAll(
				false,
				false,
				true
				);
		
	}
	
	@Test
	/**
	 * Exepected to fail at exec: wrong parameter p
	 */
	public void testWorkflowWSwrongP() {
		
		(new BasicTestWorkflowWS(500, 4, 1.2)).execAll(
				false,
				false,
				true
				);
		
	}
	
	@Test
	public void testWorkflowWSwrongN() {
		
		(new BasicTestWorkflowWS(-2, 4, 0.1)).execAll(
				false,
				false,
				true
				);
		
	}
	


}
