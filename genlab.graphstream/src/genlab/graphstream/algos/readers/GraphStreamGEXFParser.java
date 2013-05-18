package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGEXF;

public class GraphStreamGEXFParser extends AbstractGraphStreamGraphParser {

	public GraphStreamGEXFParser() {
		super(
				"GEXF parser",
				"parser of the GEXF format, as provided by the graphstream library"
				 );
	}
	
	

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceGEXF();
	}

}
