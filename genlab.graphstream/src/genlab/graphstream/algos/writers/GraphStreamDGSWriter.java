package genlab.graphstream.algos.writers;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;

public class GraphStreamDGSWriter extends AbstractGraphStreamGraphWriter {

	public GraphStreamDGSWriter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "DGS writer";
	}

	@Override
	public String getDescription() {
		return "graph writer in the DGS format, as implemented in the graphstream library";
	}

	@Override
	protected FileSink getGraphStreamFileSink() {
		return new FileSinkDGS();
	}

}
