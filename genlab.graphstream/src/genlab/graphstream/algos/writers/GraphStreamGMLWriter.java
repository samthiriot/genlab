package genlab.graphstream.algos.writers;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkGML;

public class GraphStreamGMLWriter extends AbstractGraphStreamGraphWriter {

	public GraphStreamGMLWriter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "GML writer";
	}

	@Override
	public String getDescription() {
		return "graph writer in the GML format, as implemented in the graphstream library";
	}

	@Override
	protected FileSink getGraphStreamFileSink() {
		return new FileSinkGML();
	}

}
