package genlab.os.algos;

import static org.junit.Assert.fail;
import genlab.os.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Detected how a file was actually open would be of use.
 * @see http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * 
 * @author Samuel THiriot
 *
 */
@RunWith(Parameterized.class)
public class TestOpenFile {

	// parameterized
	private final File f;
	private final String program;
	
	public TestOpenFile(File f, String program) {
		this.f = f;
		this.program = program;
	}
	@Parameters
    public static Collection<Object[]> generateData() {
    	
	    return Arrays.asList(new Object[][] {
	    		{ 
	    			createFileHtmlForTest(), "firefox"
	    		}, 
	    		{ 
	    			createFileTxtForTest(), "gedit" // TODO not multiplatform !!!
	    		}
	    		
	    		} 
	    );
    }
    
	private static File createFileHtmlForTest() {
		
		File f = null;
		try {
			f = File.createTempFile("genlabtest", ".html");
		} catch (IOException e) {
			fail("unable to create a file");
			e.printStackTrace();
		}
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(f);
		} catch (FileNotFoundException e) {
			fail("unable to write a file");
			e.printStackTrace();
		}
		ps.println("<html><body><h1>open test</h1><p>nothing</p></body></html>");
		ps.close();
		
		return f;
		
	}
	
	private static File createFileTxtForTest() {
		
		File f = null;
		try {
			f = File.createTempFile("genlabtest", ".txt");
		} catch (IOException e) {
			fail("unable to create a file");
			e.printStackTrace();
		}
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(f);
		} catch (FileNotFoundException e) {
			fail("unable to write a file");
			e.printStackTrace();
		}
		ps.println("By in no ecstatic wondered disposal my speaking. Direct wholly valley or uneasy it at really. Sir wish like said dull and need make. Sportsman one bed departure rapturous situation disposing his. Off say yet ample ten ought hence. Depending in newspaper an september do existence strangers.\nTotal great saw water had mirth happy new. Projecting pianoforte no of partiality is on. \nNay besides joy society him totally six. ");
		ps.close();
		
		return f;
		
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout=2000)
	public void testOpenStandard() {
		
		Utils.openFileWithDefaultEditor(f);

	}
	
	@Test(timeout=2000)
	public void testOpenDefined() {
		
		Utils.openFileWithEditor(program, f);

	}
	
	/**
	 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6970060
	 * 
	 */
	@Test(timeout=3000)
	public void testOpenFromAnotherThread() {
	
		
		final Object flagFinished = new Object();
		
		class ThreadTest extends Thread {

			public boolean finished = true;

			@Override
			public void run() {
				Utils.openFileWithDefaultEditor(f);
				synchronized (flagFinished) {
					flagFinished.notify();

				}
			}
			
		}
		ThreadTest tTest = new ThreadTest();
		tTest.start();
		while (!tTest.finished) {
			try {
				synchronized (flagFinished) {
					flagFinished.wait(1000);
				}
			} catch (InterruptedException e) {
			}	
		}
		
		
	}

}
