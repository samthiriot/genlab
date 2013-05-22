package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceLGL;

public class GraphStreamLGLParser extends AbstractGraphStreamGraphParser {


	public GraphStreamLGLParser() {
		super(
				"LGL parser",
				"parser of the LGL format, as provided by the graphstream library"
				 );
	}
	

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceLGL();
	}

}
