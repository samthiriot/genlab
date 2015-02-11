package genlab.r.algorithms;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.parameters.TextParameter;
import genlab.r.execs.ExecuteRExec;
import genlab.r.rsession.Genlab2RSession;

/**
 * TODO transform inputs from all types to Genlab type
 * TODO transform outputs from all types to genlab type
 * 
 * @author Samuel Thiriot
 *
 */
public class ExecuteRAlgorithm extends BasicAlgo {

	public static final TextParameter PARAM_SCRIPT = new TextParameter(
			"param_script", 
			"script", 
			"a value R script", 
			""
			);
	

	public static final InputOutput<Object> INPUT_ANYTHING = new InputOutput<Object>(
			AnythingFlowType.SINGLETON, 
			"anything", 
			"any data to analyze", 
			"the data to use as inputs for the script",
			true
			);
	
	public static final InputOutput<Object> OUTPUT = new InputOutput<Object>(
			AnythingFlowType.SINGLETON,
			"out", 
			"out", 
			"the result of the last computation"
			);
	
	
	public ExecuteRAlgorithm() {
		super(
				"compute R", 
				"compute any R script", 
				ExistingAlgoCategories.ANALYSIS, 
				null, 
				null
				);
		
		inputs.add(INPUT_ANYTHING);
		
		outputs.add(OUTPUT);
		
		registerParameter(PARAM_SCRIPT);
	}


	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new ExecuteRExec(execution, algoInstance);
	}
	
	@Override
	public boolean isAvailable() {
		return Genlab2RSession.isRAvailable();
	}



}
