package genlab.graphstream.algos.readers;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.graphstream.stream.file.FileSource;

public class AbstractGraphstreamGraphParserExecution extends
		AbstractAlgoExecution {
	
	protected final FileSource filesource;

	protected boolean cancelled = false;
	
	public AbstractGraphstreamGraphParserExecution(
			IExecution exec,
			IAlgoInstance algoInst, 
			FileSource filesource
			) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		this.filesource = filesource;
	}

	@Override
	public void run() {

		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress);
		setResult(result);
		
		// decode parameters
		final File file = (File) getInputValueForInput(AbstractGraphStreamGraphParser.PARAM_FILE);
		
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
				if (cancelled) {
					progress.setComputationState(ComputationState.FINISHED_CANCEL);
					return;
				}
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

	}

	@Override
	public void kill() {
		cancelled = true;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

}
