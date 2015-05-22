package genlab.algog.gui.misc.exec;

import genlab.algog.gui.misc.algos.PlotPhenotypeAlgo;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.algos.AbstractOpenViewContinuousAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public class PlotPhenotypeExec extends AbstractOpenViewContinuousAlgoExec {

	// the table loaded from a continuous update, or a sequential update.
	private GenlabTable table;
	
	public PlotPhenotypeExec(IExecution exec, IAlgoInstance algoInst, String viewId) {
		super(exec, algoInst, viewId);
		
	}

	
	protected void loadDataSuccessiveFromInput() {
		table = (GenlabTable)getInputValueForInput(PlotPhenotypeAlgo.INPUT_TABLE);

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


	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}


}
