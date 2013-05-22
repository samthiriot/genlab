package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceNCol;

public class GraphStreamNCOLParser extends AbstractGraphStreamGraphParser {

	public GraphStreamNCOLParser() {
		super(
				"NCOL parser",
				"parser of the NCOL format, as provided by the graphstream library"
				 );
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceNCol();
	}

}
