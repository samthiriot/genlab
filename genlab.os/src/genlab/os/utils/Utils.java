package genlab.os.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Utils {

	private Utils() {

	}

	public static void openFileWithDefaultEditor(File f) {
		
		Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
	          desktop = Desktop.getDesktop();
        } else {
        	return;
        }
		
		try {
			desktop.open(f);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		Thread.yield();
		
		System.err.println("back !");
	}
	
	public static void openFileWithEditor(String command, File f) {
		
		
		try {
			Process p = Runtime.getRuntime().exec(command+" "+f.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println("back");
		
	}
	
}
