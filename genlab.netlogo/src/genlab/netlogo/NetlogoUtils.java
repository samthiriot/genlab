package genlab.netlogo;

import genlab.core.commons.FileUtils;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSinkGML;

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
	
	private NetlogoUtils() {
		
	}

}
