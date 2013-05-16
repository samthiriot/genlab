package genlab.graphstream.algos.readers;

import genlab.core.algos.AbstractAlgoExecution;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.ComputationResult;
import genlab.core.algos.ComputationState;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.WrongParametersException;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.graphstream.stream.file.FileSource;

public class AbstractGraphstreamGraphParserExecution extends
		AbstractAlgoExecution {
	
	protected final File file;
	protected final FileSource filesource;
	protected final String graphId;

	public AbstractGraphstreamGraphParserExecution(IAlgoInstance algoInst, File file, FileSource filesource, String graphId) {
		super(algoInst, new ComputationProgressWithSteps(algoInst.getAlgo()));
		this.file = file;
		this.filesource = filesource;
		this.graphId = graphId;
	}

	@Override
	public void run() {

		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst.getAlgo(), progress);
		
		// init our sink which will process events from the filesource
		GraphstreamConvertors.GenLabGraphSink ourSink = new GraphstreamConvertors.GenLabGraphSink("opened", result.getMessages());
		
		// that we listen to
		filesource.addSink(ourSink);

		// attempt to open the file
		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new WrongParametersException("Unable to load file from " + file.getAbsolutePath() +
				" (" + e.getLocalizedMessage() + ")");
		}
		
		// actually load the graph
		try {
			filesource.begin(is);
			while (filesource.nextEvents()) {
				// nothing to do
			}
			filesource.end();
		} catch (IOException e) {
			throw new WrongParametersException("Error while parsing a graph from " + file.getAbsolutePath()+ " (" + e.getLocalizedMessage() + ")");
		} catch (Exception e) {
			throw new WrongParametersException("Error while parsing a graph from " +
					file.getAbsolutePath()+ " (" + e.getLocalizedMessage() + ")");
		} catch(Throwable e) {
			throw new WrongParametersException("Error while parsing a graph from " +
					file.getAbsolutePath()+ " (" + e.getLocalizedMessage() + ")");
		} 
		
		// ended !
		result.setResult(AbstractGraphStreamGraphParser.OUTPUT_GRAPH, ourSink.getGraph());

		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);

		setResult(result);
	}

}
