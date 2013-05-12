package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceEdge;
import org.graphstream.stream.file.FileSourceGEXF;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourcePajek;

/**
 * TODO add directed parameter
 * @author B12772
 *
 */
public class GraphStreamEdgeParser extends AbstractGraphStreamGraphParser {

	@Override
	public String getName() {
		return "Edge parser";
	}

	@Override
	public String getDescription() {
		return "parser of the Edge format, as provided by the graphstream library";
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceEdge();
	}

}
