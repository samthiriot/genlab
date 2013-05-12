package genlab.core.commons;

import java.io.File;

public class FileUtils {

	
	public static final String getExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		
		final int index = indexOfExtension(filename);
		
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}
	
	 public static int indexOfExtension(final String filename) {
		 if (filename == null) {
			 return -1;
		 }
		 final int extensionPos = filename.lastIndexOf(".");
		 final int lastSeparator = indexOfLastSeparator(filename);
		 return lastSeparator > extensionPos ? -1 : extensionPos;
	}
	 
	 public static int indexOfLastSeparator(final String filename) {
		if (filename == null) {
			 return -1;
		}
		return filename.lastIndexOf(File.separator);
	}
	
}
