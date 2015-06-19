package genlab.netlogo.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.FileParameter;
import genlab.core.parameters.IntParameter;
import genlab.netlogo.exec.NetlogoModelExec;
import genlab.netlogo.exec.SIRModelExec;
import genlab.netlogo.inst.NetlogoModelInstance;

public class NetlogoModelAlgo extends BasicAlgo {

	public static final FileParameter PARAM_NETLOGO_MODEL = new FileParameter(
			"param_modelfile", 
			"netlogo file", 
			"the file containing the netlogo model", 
			null
			);
	
	public static final IntegerInOut OUTPUT_DURATION = new IntegerInOut(
			"out_duration", 
			"duration", 
			"duration of the run in steps"
			);
	
	public static final IntParameter PARAM_MAX_STEPS = new IntParameter(
			"param_maxiteration", 
			"max steps", 
			"maximum duration of the simulation in steps", 
			1000,
			1
			);
	
	public static final BooleanParameter PARAM_GUI = new BooleanParameter(
			"param_gui", 
			"see GUI", 
			"see the graphical user interface", 
			Boolean.FALSE
			);
	
	public NetlogoModelAlgo() {
		super(
				"run model (Netlogo)", 
				"run any Netlogo model", 
				ExistingAlgoCategories.MODELS, 
				null, 
				null
				);
		
		outputs.add(OUTPUT_DURATION);
		
		registerParameter(PARAM_NETLOGO_MODEL);
		registerParameter(PARAM_MAX_STEPS);
		registerParameter(PARAM_GUI);
	}
	
	

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new NetlogoModelInstance(this, workflow);
	}



	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new NetlogoModelInstance(this, workflow, id);
	}



	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new NetlogoModelExec(execution, algoInstance);
	}

}
