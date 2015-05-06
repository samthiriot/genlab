package genlab.core.commons;

import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * TODO propose a "link or copy" command
 * 
 * @author Samuel Thiriot
 *
 */
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
	
	public static final String extractFilenameWithoutExtension(final String filename) {
		
		String filenameOnly = extractFilename(filename);
		
		final int index = indexOfExtension(filenameOnly);
		
		if (index == -1) {
			return "";
		} else {
			return filenameOnly.substring(0, index);
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
	 
	 public static boolean copyFiles (InputStream in, File destFile) {
		 
		try {
			OutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) != -1) {
				    out.write(buffer, 0, len);
				}
				out.flush();

				return true;
			} finally {
				out.close();
				in.close();
			}
		} catch (FileNotFoundException e) {
			throw new ProgramException("error during file copy: "+e.getMessage(), e);
		} catch (IOException e) {
			throw new ProgramException("error during file copy: "+e.getMessage(), e);
		}
		
		
	 }
	 
	 public static boolean copyFiles (File sourceFile, File destFile) {
		 
		GLLogger.debugTech("copying file "+sourceFile+" to "+destFile, FileUtils.class);
		try {
			 org.apache.commons.io.FileUtils.copyFile(sourceFile, destFile);
		
		} catch (IOException e) {
			GLLogger.errorTech("error during the file copy from "+sourceFile+" to "+destFile+": "+e.getMessage(), FileUtils.class, e);
			return false;
		}
	    
		return true;
	 }

	public static File getHomeDirectoryFile() {
		return new File(System.getProperty("user.home"));
	}

	public static String osPath2ressourcePath(String filenameOriginal) {
		if (File.separator == "/")
			return filenameOriginal;
		else
			return filenameOriginal.replace(File.separatorChar, '/');
		
	}
	
	protected static File genlabTmpDirectory = null;
	
	/**
	 * Returns a tmp directory, always different at each call.
	 * Will be deleted at the end of the program.
	 * @return
	 */
	public static File getGenlabTmpUniqueDirectory() {
			
		// TODO jvm7: simpler method in java 7
		
		File genlabTmpDirectory = null;
		
		GLLogger.debugTech("creating the directory for tmp data...", FileUtils.class);
		try {
			// first ask for a tmp file...
			genlabTmpDirectory = File.createTempFile("genlab_tmpdata", "");
			// then delete it...
			genlabTmpDirectory.delete();
			// then create it as a directory !
			genlabTmpDirectory.mkdirs();
		} catch (IOException e) {
			GLLogger.debugTech("error while creating the directory for tmp data: "+e, FileUtils.class, e);
			throw new ProgramException("unable to create the tmp directory for genlab",e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				FileUtils.clearTmpData();
			}
			
		});
		// attempt to redirect all the other temp resources there. Not this is not guaranteed
		try {
			System.setProperty("java.io.tmpdir", genlabTmpDirectory.getAbsolutePath());
		} catch (Throwable t) {
		}
			
		return genlabTmpDirectory;
	}

	public static File getGenlabTmpDirectory() {
		
		if (genlabTmpDirectory == null) {
		
			// TODO jvm7: simpler method in java 7
			
			GLLogger.debugTech("creating the directory for tmp data...", FileUtils.class);
			try {
				// first ask for a tmp file...
				genlabTmpDirectory = File.createTempFile("genlab_tmpdata", "");
				// then delete it...
				genlabTmpDirectory.delete();
				// then create it as a directory !
				genlabTmpDirectory.mkdirs();
			} catch (IOException e) {
				GLLogger.debugTech("error while creating the directory for tmp data: "+e, FileUtils.class, e);
				throw new ProgramException("unable to create the tmp directory for genlab",e);
			}
			
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					FileUtils.clearTmpData();
				}
				
			});
			// attempt to redirect all the other temp resources there. Not this is not guaranteed
			try {
				System.setProperty("java.io.tmpdir", genlabTmpDirectory.getAbsolutePath());
			} catch (Throwable t) {
			}
			
		}
		
		return genlabTmpDirectory;
	}
	
	/**
	 * Creates a tmp file into a special tmp directory created for genlab (more clean !).
	 * Ensures the directory structure is less dirty; also ensures files are going to be
	 * deleted at exit 
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static File createTmpFile(String prefix, String suffix) {
		
		try {
			return File.createTempFile(
					prefix, 
					suffix, 
					getGenlabTmpDirectory()
					);
		} catch (IOException e) {
			throw new ProgramException("unable to create a tmp file: "+e, e);
		}
	}
	
	public static void clearTmpData() {
		if (genlabTmpDirectory != null) {
			GLLogger.debugTech("clearing genlab tmp data...", FileUtils.class);
			try {
				// the sub lib manages the recursive deletion in all cases (symlinks etc)
				org.apache.commons.io.FileUtils.deleteDirectory(genlabTmpDirectory);
			} catch (IOException e) {
				GLLogger.warnTech("unable to clear tmp data :-(", FileUtils.class, e);
			}
		}
	}
	
}
