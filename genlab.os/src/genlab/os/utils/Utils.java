package genlab.os.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Utils {

	private Utils() {

	}

	public static void openFileWithDefaultEditor(File f) {
		
		System.err.println("open A");

		Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
	          desktop = Desktop.getDesktop();
        } else {
        	throw new RuntimeException("Desktop is not supported");
        }

		System.err.println("open B");

		try {
			desktop.open(f);
		} catch (IOException ioe) {
			ioe.printStackTrace();
        	throw new RuntimeException("I/O error while attempting to open a file", ioe);

		}
		
		System.err.println("open C");

		Thread.yield();
		
		System.err.println("back !");
	}
	
	public static void openFileWithEditor(String command, File f) {
		
		
		try {
			Process p = Runtime.getRuntime().exec(command+" "+f.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	throw new RuntimeException("I/O error while attempting to open a file", e);
		}

		System.err.println("back");
		
	}
	
}
