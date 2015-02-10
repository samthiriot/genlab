package genlab.r.rsession;

import static org.junit.Assert.*;

import org.junit.Test;
import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
public class TestBasicsRsession {

	@Test
	public void testAccessRSession() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();
		assertNotNull(rsession);
		
		System.err.println("status: "+rsession.getStatus());
		REXP o = rsession.eval("R.version.string");
		assertNotNull(o);
		try {
			System.out.println("using R version "+o.asString());
		} catch (REXPMismatchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		
		// ensure we can read an Integer
		{
			REXP expRes = rsession.eval("1");
			try {
				assertEquals(1, expRes.asInteger());
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		{
			REXP expRes = rsession.eval("-1");
			try {
				assertEquals(-1, expRes.asInteger());
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		{
			REXP expRes = rsession.eval("2+2");
			try {
				assertEquals(4, expRes.asInteger());
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		
	}
	
	@Test
	public void testVariables() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();
		assertNotNull(rsession);
		
		System.err.println("status: "+rsession.getStatus());
		REXP o = rsession.eval("R.version.string");
		assertNotNull(o);
		try {
			System.out.println("using R version "+o.asString());
		} catch (REXPMismatchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		
		// ensure we can set variables in 2 modes and sum
		{			
			rsession.eval("a <- 12");
			rsession.set("b", 2);
			REXP expRes = rsession.eval("a+b");

			try {
				assertEquals(14, expRes.asInteger());
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
				
	}
	

	@Test
	public void testPrint() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();
		assertNotNull(rsession);
		
		{
			REXP expRes = rsession.eval("print(\"toto\")");
			try {
				assertEquals("toto", expRes.asString());
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	

	@Test
	public void testLoadIGraph() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();
		assertNotNull(rsession);
	
		REXP o = rsession.eval("library(igraph)");
		assertNotNull(o);
			
		{
			REXP expRes = rsession.eval("average.path.length(watts.strogatz.game(size=100,dim=1,nei=2,p=0.1))");
			try {
				double d = expRes.asDouble();
				assertTrue(d > 3);
				assertTrue(d < 7);
			} catch (REXPMismatchException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	class TestRThread extends Thread {

		final int loops = 20;
		final int pause = 200;
		
		final double a;
		final double b;
		final Rsession rsession;
		
		public TestRThread(String name, double a, double b) {
			super(name);
			this.a = a;
			this.b = b;
			this.rsession = Genlab2RSession.createNewLocalRSession();
			setDaemon(false);
		}
		
		@Override
		public void run() {
			
			for (int j = 0; j < loops; j++) {
				
				System.out.println(getName()+" loop "+j);
				
				rsession.set("x",a);
				
				try {
			
					Thread.sleep((long)(pause*Math.random()));
			
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				rsession.set("y",b);
				REXP expRes = rsession.eval("x+y");
				if (rsession.getStatus() == Rsession.STATUS_ERROR) {
					fail("error in R");
					System.err.println("error in R!");
					return;
				}
				System.out.println(getName()+" eval "+j);
				try {
					double d = expRes.asDouble();
					assertEquals("session was corrupted", a+b, d, 0.0001);
				} catch (REXPMismatchException e) {
					e.printStackTrace();
					fail(e.getMessage());
					System.err.println("error in R!");

				}
			
				
			}
			
			System.err.println("end loop");

		}

		@Override
		protected void finalize() throws Throwable {
			rsession.end();
			super.finalize();
		}
		
	}
	
	@Test
	public void testMultithread() {
	
		TestRThread thread1 = new TestRThread("1", 1, 3);
		TestRThread thread2 = new TestRThread("2", 5, 9);
		TestRThread thread3 = new TestRThread("3", 100, 101);
		
		thread1.start();
		thread2.start();
		thread3.start();
		
		try {
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
