package genlab.gui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IDisplayAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.gui.Activator;

import org.osgi.framework.Bundle;

/**
 * Displays any input as text displayed in a console displayed as a view.
 * 
 * TODO reduce !
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphicalConsoleAlgo extends BasicAlgo implements IDisplayAlgo  {


	public static final InputOutput<Object> INPUT = new InputOutput<Object>(
			AnythingFlowType.SINGLETON, 
			"anything", 
			"any data to display", 
			"any data to be displayed into the console",
			true
			);
	
	public GraphicalConsoleAlgo() {
		super(
				"graphical console", 
				"displays everything into the console dedicated to this run",
				null,
				ExistingAlgoCategories.DISPLAY.getTotalId(),
				"/icons/console_view.gif"
				);
		
		inputs.add(INPUT);
		

	}
	
	
	
	@Override
	public IAlgoExecution createExec(
			IExecution execution, 
			AlgoInstance algoInstance
			) {
		
		return new GraphicalConsoleExec(execution, algoInstance);
	}



	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}
	

}
