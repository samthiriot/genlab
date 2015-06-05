package genlab.netlogo;

import genlab.core.commons.FileUtils;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSinkGML;

import scala.annotation.target.getter;

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
	
	public static File writeGraphToNetlogoGML(IGenlabGraph g, ListOfMessages messages) {

		File tmpFile = FileUtils.createTmpFile("netlogo_", ".net");
		
		FileSinkGML fileSink = new FileSinkGML();
		try {
			fileSink.writeAll(
					GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(g, messages), 
					tmpFile.getAbsolutePath()
					);
		} catch (IOException e) {
			throw new RuntimeException("error while writing the network to a file for Netlogo", e);
		}
		
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

}
