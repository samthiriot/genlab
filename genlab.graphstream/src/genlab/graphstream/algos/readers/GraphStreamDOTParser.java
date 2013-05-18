package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

public class GraphStreamDOTParser extends AbstractGraphStreamGraphParser {

	public GraphStreamDOTParser() {
		super(
				 "DOT parser",
				 "parser of the DOT format, as provided by the graphstream library"
				 );
	}
	

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceDOT();
	}

}
