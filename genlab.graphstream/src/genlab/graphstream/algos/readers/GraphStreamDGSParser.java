package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;

public class GraphStreamDGSParser extends AbstractGraphStreamGraphParser {

	public GraphStreamDGSParser() {
		super(
				 "DGS parser",
				 "parser of the DGS format, as provided by the graphstream library"
				 );
	}
	

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceDGS();
	}

}
