package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphStreamPajekParser extends AbstractGraphStreamGraphParser {


	public GraphStreamPajekParser() {
		super(
				"pajek parser",
				"parser of the pajek format, as provided by the graphstream library"
				 );
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourcePajek();
	}

}
