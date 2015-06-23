package genlab.r.execs;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.r.algorithms.ExecuteRAlgorithm;
import genlab.r.rsession.Genlab2RSession;

import java.util.Map;
import java.util.StringTokenizer;

import org.math.R.Logger;
import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

public class ExecuteRExec extends AbstractAlgoExecutionOneshot {

	public ExecuteRExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected Object convertToRValue(Object o) {
		return o;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);

		Rsession rsession = null;
		try {
				
			// retrieve inputs
			final String paramScript = (String)algoInst.getValueForParameter(ExecuteRAlgorithm.PARAM_SCRIPT);
			final Map<IConnection,Object> inputValues = getInputValuesForInput(ExecuteRAlgorithm.INPUT_ANYTHING);
			
			// start script
			rsession = Genlab2RSession.createNewLocalRSession();
			// monitor what happens
			rsession.addLogger(new Logger() {
				
				@Override
				public void println(String arg0) {
					messages.debugUser("R:"+arg0, getClass());
				}
				
			});
			
			//rsession.log("test log");
			
			// define inputs in R
			for (IConnection connectionIn : inputValues.keySet()) {
				
				String name = connectionIn.getFrom().getAlgoInstance().getName().replaceAll(" ", "_")
								+"."+
								connectionIn.getFrom().getMeta().getName().replaceAll(" ", "_")
								;
				Object value = convertToRValue(inputValues.get(connectionIn));
				
				this.messages.traceTech("defining in R the variable "+name+" to value "+value, getClass());
				
				rsession.set(
						name, 
						value
						);
				
				if (connectionIn.getFrom().getAlgoInstance().getOutputInstances().size() == 1) {
					// add shortcut
					name = connectionIn.getFrom().getAlgoInstance().getName().replaceAll(" ", "_");
					
					this.messages.traceTech("defining in R the variable "+name+" to value "+value, getClass());
					
					rsession.set(
							name, 
							value
							);
						
				}
			}
			
			// execute
			StringTokenizer st = new StringTokenizer(paramScript);
			Object currentResult = null;
			while (st.hasMoreTokens()) {
				
				final String evaluated = st.nextToken();
				try {
					REXP o = rsession.eval(evaluated);
					if (rsession.getStatus() == Rsession.STATUS_ERROR || o == null) {
						messages.errorUser("error while evaluating in R \""+evaluated+"\"", getClass());
						progress.setComputationState(ComputationState.FINISHED_FAILURE);
						return;
					}
					// TODO rsession.getStatus()
					// set result !
					messages.debugUser("current result for \""+evaluated+"\": "+rsession.cast(o), getClass());
					currentResult = Rsession.cast(o);	
				} catch (REXPMismatchException e) {
					messages.errorUser("unable to evaluate \""+evaluated+"\": "+e.getLocalizedMessage(), getClass(), e);
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
					return;
				}
			}
			
			// finished !
			ComputationResult res = new ComputationResult(algoInst, progress, messages);
			res.setResult(ExecuteRAlgorithm.OUTPUT, currentResult);
			setResult(res);
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during R exec "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		} finally {
			if (rsession != null) 
				rsession.end();
		}
		

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
