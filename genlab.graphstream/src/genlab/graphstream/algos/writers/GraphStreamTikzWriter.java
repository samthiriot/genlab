package genlab.graphstream.algos.writers;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkTikZ;

public class GraphStreamTikzWriter extends AbstractGraphStreamGraphWriter {

	public GraphStreamTikzWriter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Tikz writer";
	}

	@Override
	public String getDescription() {
		return "graph writer in the Tikz format, as implemented in the graphstream library";
	}

	@Override
	protected FileSink getGraphStreamFileSink() {
		return new FileSinkTikZ();
	}

}
