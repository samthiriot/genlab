package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourcePajek;

public class GraphStreamDGSParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "DGS parser";
	}

	@Override
	public String getDescription() {
		return "parser of the DGS format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceDGS();
	}

}
