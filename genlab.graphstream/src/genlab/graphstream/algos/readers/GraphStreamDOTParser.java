package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphStreamDOTParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "DOT parser";
	}

	@Override
	public String getDescription() {
		return "parser of the DOT format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceDOT();
	}

}
