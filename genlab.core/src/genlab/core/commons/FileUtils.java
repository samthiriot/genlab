package genlab.core.commons;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class FileUtils {

	/**
	 * Returns the "path" part of a filename; if the parameter is relative, 
	 * the result will also be.
	 * @param filename
	 * @return
	 */
	public static final String extractPath(final String filename) {
		final int index = filename.lastIndexOf(File.separator);
		if (index < 0) {
			return "";
		} else {
			return filename.substring(0,index);
		}
	}
	
	public static final String extractFilename(final String filename) {
		final int index = filename.lastIndexOf(File.separator);
		if (index < 0) {
			return filename;
		} else {
			return filename.substring(index+1);
		}
	}
	
	/**
	 * Returns the extension of the filename
	 * @param filename
	 * @return
	 */
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
