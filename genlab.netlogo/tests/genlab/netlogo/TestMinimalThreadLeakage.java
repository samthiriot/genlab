package genlab.netlogo;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.nlogo.api.CompilerException;
import org.nlogo.api.LogoException;
import org.nlogo.headless.HeadlessWorkspace;

public class TestMinimalThreadLeakage {

	private final static String NETLOGO_MODEL_FILENAME = "./ressources/models/Virus on a Network test.nlogo";
	private final static String NETWORK_GRAPHML = "../../testdata/networks/ws1.graphml.net";
	private final static String NETWORK_GML = "../../testdata/networks/ws1.gml.net";
	
	private HeadlessWorkspace workspace = null;
	
	private void runANetworkLoad(String networkFile, String format) {

		// create workspace
		//HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
		
		if (workspace == null) {
			
			workspace = HeadlessWorkspace.newInstance();
			
			// load model
			try {
				workspace.open(NETLOGO_MODEL_FILENAME);
			} catch (Exception e) {
				e.printStackTrace();
				fail("error while opening model: "+e.getMessage());
				
			}
			
		}
		
		// parameter, setup and run model
		try {
			workspace.command("set network-filename \""+networkFile+"\"");
			workspace.command("set is-graphical true");
			workspace.command("set initial-outbreak-size 10");
			workspace.command("set virus-spread-chance 100");
			workspace.command("set virus-check-frequency 1");
			workspace.command("set recovery-chance 10");
			workspace.command("set gain-resistance-chance 100");
			workspace.command("setup-"+format) ;
			workspace.command("repeat 500 [ go ]") ;
		} catch (Exception e) {
			e.printStackTrace();
			fail("netlogo error: "+e.getMessage());
		}
		
		/*try {
			workspace.dispose();
		} catch (InterruptedException e) {
			fail("error during dispose");
		}*/
		
	}
	
	@Test
	public void testThreadLeakageGraphML() {
		// heating period (so we don't count the thread created for activation in a normal process)
		for (int i=0; i<5; i++) {
			runANetworkLoad(NETWORK_GRAPHML, "graphml");
			System.err.println("heating: threads="+Thread.activeCount());
		}
		// reference threads count
		int threadsCount = Thread.activeCount();
		System.err.println("reference: threads="+Thread.activeCount());
		for (int i=0; i<10; i++) {
			runANetworkLoad(NETWORK_GRAPHML, "graphml");
			System.err.println("evaluation: threads="+Thread.activeCount());
		}
		int finalThreadCount = Thread.activeCount();
		System.err.println("final: threads="+Thread.activeCount());
		Assert.assertTrue(
				"threads were created: "+((finalThreadCount-threadsCount)/10), 
				finalThreadCount == threadsCount
				);
	}
	
	@Test
	public void testThreadLeakageGML() {
		// heating period (so we don't count the thread created for activation in a normal process)
		for (int i=0; i<5; i++) {
			runANetworkLoad(NETWORK_GML, "gml");
			System.err.println("heating: threads="+Thread.activeCount());
		}
		// reference threads count
		int threadsCount = Thread.activeCount();
		System.err.println("reference: threads="+Thread.activeCount());
		for (int i=0; i<10; i++) {
			runANetworkLoad(NETWORK_GML, "gml");
			System.err.println("evaluation: threads="+Thread.activeCount());
		}
		int finalThreadCount = Thread.activeCount();
		System.err.println("final: threads="+Thread.activeCount());
		Assert.assertTrue(
				"threads were created: "+((finalThreadCount-threadsCount)/10), 
				finalThreadCount == threadsCount
				);
	}

}
