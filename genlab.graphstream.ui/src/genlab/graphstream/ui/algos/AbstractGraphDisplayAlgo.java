package genlab.graphstream.ui.algos;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.FileParameter;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.graphstream.algos.GraphStreamAlgo;
import genlab.graphstream.ui.Activator;
import genlab.graphstream.ui.views.AbstractGraphView;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.io.File;

import org.osgi.framework.Bundle;

/**
 * The algorithm that takes a graph as input
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractGraphDisplayAlgo extends GraphStreamAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public static final FileParameter PARAM_STYLESHEET = new FileParameter(
			"stylesheet", 
			"stylesheet", 
			"the stylesheet that enables the tuning of the display", 
			null
			);
	
	public AbstractGraphDisplayAlgo(String name, String description) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.DISPLAY_GRAPH
				);
		
		inputs.add(INPUT_GRAPH);
		
		registerParameter(PARAM_STYLESHEET);

	}
	
	
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}



	protected abstract String getViewId();
	

	@Override
	public IAlgoExecution createExec(
			final IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new AbstractOpenViewAlgoExec(execution, algoInstance, getViewId()) {
			
			IGenlabGraph glGraph = null;

			@Override
			protected void displayResultsSync(AbstractViewOpenedByAlgo theView) {
				
				GLLogger.tipUser("if the window opened to display the graph remains gray, please try to move or resize it to correct the problem (known bug, sorry)", getClass());
				
				GLLogger.traceTech("displaying the graph...", getClass());
				AbstractGraphView gv = (AbstractGraphView)theView;
				
				//IGenlabGraph glGraph = (IGenlabGraph)getInputValueForInput(INPUT_GRAPH);
				
				File fileCss = (File)algoInstance.getValueForParameter(PARAM_STYLESHEET);
				String filenameCss = null;
				if (fileCss != null && fileCss.exists()) {
					execution.getListOfMessages().debugUser("a stylesheet was provided, verifying it...", getClass());
					if (!fileCss.exists()) 
						throw new WrongParametersException("the file provided for the stylesheet does not exists");
					if (!fileCss.isFile()) 
						throw new WrongParametersException("a file is expected for the stylesheet");
					if (!fileCss.canRead()) 
						throw new WrongParametersException("can not read the file provided for the stylesheet");
					filenameCss = fileCss.getAbsolutePath();
				} 
				gv.displayGraph(glGraph, filenameCss, execution.getListOfMessages(), getProgress());
			}

			@Override
			public long getTimeout() {
				return 1000*5;
			}

			@Override
			protected void loadDataSuccessiveFromInput() {
				
				glGraph = (IGenlabGraph)getInputValueForInput(INPUT_GRAPH);

			}

			@Override
			protected void displayResultsSyncReduced(
					AbstractViewOpenedByAlgo theView,
					IAlgoExecution executionRun,
					IConnectionExecution connectionExec, Object value) {
				// TODO Auto-generated method stub
				
				// TODO implement the display reduced for graphstream ?
			}
		};
	}

	
}
