package genlab.gui.jfreechart.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.jfreechart.algos.ScatterPlotAlgo;
import genlab.gui.jfreechart.instance.ScatterPlotAlgoInstance;
import genlab.gui.jfreechart.views.ScatterView;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public class ScatterPlotExec extends AbstractJFreeChartAlgoExec {

	// the table loaded from a continuous update, or a sequential update.
	private GenlabTable table;
	private boolean parametersDefined = false;

	public ScatterPlotExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
		
	}

	protected void adaptParametersForData(IAlgoInstance algoInst, GenlabTable table) {
		
		if (table == null)
			return;
		
		ScatterPlotAlgoInstance algoInstance = (ScatterPlotAlgoInstance)algoInst;
		
		algoInstance.getParameterColumnX().setItems(table.getColumnsId());
		algoInstance.getParameterColumnY().setItems(table.getColumnsId());
		
		parametersDefined = true;
	}
	
	protected void loadDataSuccessiveFromInput() {
		table = (GenlabTable)getInputValueForInput(ScatterPlotAlgo.INPUT_TABLE);

	}
		
	protected void setDataFromContinuousUpdate(IAlgoExecution continuousProducer,
			Object keyWave, IConnectionExecution connectionExec, Object value) {

		table = (GenlabTable)value;

	}

	
	@Override
	protected void displayResultsSync(AbstractViewOpenedByAlgo theView) {
		
		if (!parametersDefined)
			adaptParametersForData(algoInst, table);
		
		((ScatterView)theView).setData(
				algoInst,
				table
				);
	}

	@Override
	protected void displayResultsSyncReduced(AbstractViewOpenedByAlgo theView,
			IAlgoExecution executionRun, IConnectionExecution connectionExec,
			Object value) {
		// TODO Auto-generated method stub
		
	}


}
