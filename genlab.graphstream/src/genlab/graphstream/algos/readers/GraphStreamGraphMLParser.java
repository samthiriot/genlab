package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;

public class GraphStreamGraphMLParser extends AbstractGraphStreamGraphParser {

	public GraphStreamGraphMLParser() {
		super(
				"GraphML parser",
				"parser of the GraphML format, as provided by the graphstream library"
				 );
	}
	

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceGraphML();
	}

}
