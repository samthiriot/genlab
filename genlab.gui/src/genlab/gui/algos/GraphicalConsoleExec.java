package genlab.gui.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ConnectionExec;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.gui.views.AbstractViewOpenedByAlgo;
import genlab.gui.views.ConsoleView;

import java.util.Map;
import java.util.Map.Entry;


public class GraphicalConsoleExec extends AbstractOpenViewAlgoExec {

	
	public GraphicalConsoleExec(
			IExecution exec, 
			IAlgoInstance algoInst) {
		
		super(exec, algoInst, ConsoleView.VIEW_ID);

		
	}
	
	@Override
	protected void displayResults(AbstractViewOpenedByAlgo theView) {
		
		ConsoleView cv = (ConsoleView)theView;
		

		if (cv == null) {
			getResult().getMessages().warnUser("unable to find the info to display", getClass());
			getProgress().setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}
		
		// just read the values and display them
		Map<IConnection, Object> values = getInputValuesForInput(GraphicalConsoleAlgo.INPUT);
		
		getProgress().setProgressTotal(values.size());
		
	
		StringBuffer sb = new StringBuffer();
		for (Entry<IConnection,Object> entry : values.entrySet()) {
			
			IInputOutputInstance input = entry.getKey().getFrom();
			sb.append("result for ");
			sb.append(input.getAlgoInstance().getName());
			sb.append(" / ");
			sb.append(input.getMeta().getName());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append("\n");
			
			getProgress().incProgressMade();
		}
		
		cv.write(sb.toString());
		
		// in fact, we have nothing to do here
		// just set result to finished
		setResult(null);
		getProgress().setComputationState(ComputationState.FINISHED_OK);
		
	}

	@Override
	public long getTimeout() {
		return 1000*5;
	}


	

}
