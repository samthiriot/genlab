package genlab.netlogo;

import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;
import genlab.jung.utils.JungWriters;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.graphstream.stream.file.FileSinkGML;
import org.osgi.framework.Bundle;

public class NetlogoUtils {

	public static String toNetlogoString(Object v) {
		
		if (v instanceof String) {
			StringBuffer sb = new StringBuffer();
			sb.append("\"");
			sb.append(v);
			sb.append("\"");
			return sb.toString();
		}
		
		return v.toString();
	}
	
	/**
	 * Writes a genlab graph to a graph readable by Netlogo in GraphML
	 * @param g
	 * @param messages
	 * @return
	 */
	public static File writeGraphToNetlogoGraphML(IGenlabGraph g, ListOfMessages messages) {

		File tmpFile = FileUtils.createTmpFile("netlogo_", ".net");
		
		JungWriters.writeGraphAsGraphML(g, tmpFile);
		
		return tmpFile;

	}
	
	/**
	 * writes a genlab graph to a graph readable by Netlogo in GML
	 * @param g
	 * @param messages
	 * @return
	 */
	public static File writeGraphToNetlogoGML(IGenlabGraph g) {
 		
		File tmpFile = FileUtils.createTmpFile("netlogo_", ".net");
		FileSinkGML fileSink = new FileSinkGML();
		try {
			fileSink.writeAll(
						GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(g, null), 
						tmpFile.getAbsolutePath()
						);
		} catch (IOException e) {
			throw new RuntimeException("error while writing the network to a file for Netlogo", e);
		}
		/*
		try {
			fileSink.end();
		} catch (IOException e) {
			throw new ProgramException("error while writing the graph in a file: "+e.getMessage());
		}*/
		return tmpFile;
	}
	 
	
	public static String findAbsolutePathForRelativePath(String relative) {
		File f;
		
		// find it immediately from the current path
		f = new File(relative);
		if (f.exists()) 
			return f.getAbsolutePath();
		
		// load as file in our directory
		String pathPackaged = "plugins"+File.separator+"genlab.netlogo"+File.separator+relative;
		f = new File(pathPackaged);
		if (f.exists())
			return f.getAbsolutePath();
		
		return null;

	}
	
	private NetlogoUtils() {
		
	}

	public static File findFileInPlugin(String resourcePath) {
		Bundle bundle = Activator.getDefault().getBundle();
		URL fileURL = bundle.getEntry(resourcePath);
		File file = null;
		try {
		    file = new File(FileLocator.resolve(fileURL).getFile());
		} catch (IOException e) {
		    throw new ProgramException("unable to find the file for the Netlogo model", e);
		} 
		return file;
	}

}
