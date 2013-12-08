package genlab.gui.jfreechart.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.jfreechart.algos.ScatterPlotAlgo;
import genlab.gui.jfreechart.views.ScatterView;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public class ScatterPlotExec extends AbstractJFreeChartAlgoExec {

	public ScatterPlotExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
		
	}

	
	@Override
	protected void displayResults(AbstractViewOpenedByAlgo theView) {
		((ScatterView)theView).setData(
				algoInst,
				(GenlabTable)getInputValueForInput(ScatterPlotAlgo.INPUT_TABLE)
				);
	}

}
