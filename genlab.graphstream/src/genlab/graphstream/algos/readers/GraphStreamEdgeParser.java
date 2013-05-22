package genlab.graphstream.algos.readers;

import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

/**
 * TODO add directed parameter
 * @author B12772
 *
 */
public class GraphStreamEdgeParser extends AbstractGraphStreamGraphParser {

	public GraphStreamEdgeParser() {
		super(
				 "Edge parser",
				 "parser of the Edge format, as provided by the graphstream library"
				 );
	}

	@Override
	protected FileSource getGraphStreamFileSource() {
		return new FileSourceEdge();
	}

}
