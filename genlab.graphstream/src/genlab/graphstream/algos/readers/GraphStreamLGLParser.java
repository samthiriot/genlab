package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphStreamLGLParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "LGL parser";
	}

	@Override
	public String getDescription() {
		return "parser of the LGL format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceLGL();
	}

}
