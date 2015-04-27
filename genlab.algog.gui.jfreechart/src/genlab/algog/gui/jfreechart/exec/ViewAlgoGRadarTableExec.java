package genlab.algog.gui.jfreechart.exec;

import genlab.algog.gui.jfreechart.algos.AlgoGPlotRadarAlgo;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.jfreechart.exec.AbstractJFreeChartAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public class ViewAlgoGRadarTableExec extends AbstractJFreeChartAlgoExec {

	// the table loaded from a continuous update, or a sequential update.
	private GenlabTable table;
	
	public ViewAlgoGRadarTableExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
		
	}

	
	protected void loadDataSuccessiveFromInput() {
		table = (GenlabTable)getInputValueForInput(AlgoGPlotRadarAlgo.INPUT_TABLE);

	}
		
	protected void setDataFromContinuousUpdate(IAlgoExecution continuousProducer,
			Object keyWave, IConnectionExecution connectionExec, Object value) {

		table = (GenlabTable)value;

	}

	@Override
	protected void displayResultsSync(AbstractViewOpenedByAlgo theView) {
		
		theView.receiveData(table);
	}
	

	@Override
	protected void displayResultsSyncReduced(AbstractViewOpenedByAlgo theView,
			IAlgoExecution executionRun, IConnectionExecution connectionExec,
			Object value) {
		// TODO Auto-generated method stub
		
	}


}
