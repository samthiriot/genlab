package genlab.graphstream.algos.writers;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkTikZ;

public final class GraphStreamTikzWriter extends AbstractGraphStreamGraphWriter {

	public GraphStreamTikzWriter() {
		super(
				"Tikz writer",
				"graph writer in the Tikz format, as implemented in the graphstream library"
				);
	}

	@Override
	protected FileSink getGraphStreamFileSink() {
		return new FileSinkTikZ();
	}
	
	@Override
	protected String getFilenameExtension() {
		return ".tikz";
	}

}
