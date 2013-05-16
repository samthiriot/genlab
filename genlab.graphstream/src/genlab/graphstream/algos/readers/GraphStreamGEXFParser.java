package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGEXF;

public class GraphStreamGEXFParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "GEXF parser";
	}

	@Override
	public String getDescription() {
		return "parser of the GEXF format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceGEXF();
	}

}
