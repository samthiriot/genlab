package genlab.graphstream.algos.writers;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkGML;

public class GraphStreamGMLWriter extends AbstractGraphStreamGraphWriter {

	public GraphStreamGMLWriter() {
		super(
				"GML writer",
				"graph writer in the GML format, as implemented in the graphstream library"
				);
	}


	@Override
	protected FileSink getGraphStreamFileSink() {
		return new FileSinkGML();
	}

}
