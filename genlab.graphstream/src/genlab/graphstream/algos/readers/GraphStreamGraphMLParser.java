package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGraphML;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphStreamGraphMLParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "GraphML parser";
	}

	@Override
	public String getDescription() {
		return "parser of the GraphML format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceGraphML();
	}

}
