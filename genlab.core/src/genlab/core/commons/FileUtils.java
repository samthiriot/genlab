package genlab.core.commons;

import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
	 
	 public static boolean copyFiles (File sourceFile, File destFile) {
		 
		GLLogger.debugTech("copying file "+sourceFile+" to "+destFile, FileUtils.class);
		try {
			if(!destFile.exists()) {
		        destFile.createNewFile();
		    }

		    FileChannel source = null;
		    FileChannel destination = null;
		    try {
		        source = new FileInputStream(sourceFile).getChannel();
		        destination = new FileOutputStream(destFile).getChannel();

		        // previous code: destination.transferFrom(source, 0, source.size());
		        // to avoid infinite loops, should be:
		        long count = 0;
		        long size = source.size();              
		        while((count += destination.transferFrom(source, count, size-count))<size);
		    } finally {
		        if(source != null) {
		            source.close();
		        }
		        if(destination != null) {
		            destination.close();
		        }
		    }
		
		} catch (IOException e) {
			GLLogger.errorTech("error during the file copy from "+sourceFile+" to "+destFile+": "+e.getMessage(), FileUtils.class, e);
			return false;
		}
	    
		return true;
	 }

	public static File getHomeDirectoryFile() {
		return new File(System.getProperty("user.home"));
	}
	
}
