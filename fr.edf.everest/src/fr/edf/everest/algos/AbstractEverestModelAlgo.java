package fr.edf.everest.algos;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.io.json.JsonWriter;

import fr.edf.everest.ModelInput;
import fr.edf.everest.ModelOutput;
import fr.edf.everest.exec.EverestModelExec;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.StringParameter;

public class AbstractEverestModelAlgo extends BasicAlgo {


	public static final IntParameter PARAM_MODEL_ID = new IntParameter(
			"model_id", 
			"model id", 
			"id of the executable model to run",
			81000,
			0
			);
	

	public static final IntParameter PARAM_SCENARIO_ID = new IntParameter(
			"scenario_id", 
			"scenario id", 
			"id of the scenario to associate with (0 for none)",
			0,
			0
			);
	
	public static final StringParameter PARAM_SERVER_URL = new StringParameter(
			"server_url", 
			"server url", 
			"the base url to use for connection",
			"https://serverlocation/everest-server/"
			);

	public static final StringParameter PARAM_SERVER_URL_DOWNLOAD = new StringParameter(
			"server_url_download", 
			"server url for download", 
			"the base url to use for retrieving results",
			"https://serverlocation/data/export/results/"
			);
	
	public static final StringParameter PARAM_SERVER_LOGIN = new StringParameter(
			"server_login", 
			"login", 
			"",
			"yourlogin"
			);
	
	public static final StringParameter PARAM_SERVER_PASSWORD = new StringParameter(
			"server_password", 
			"password", 
			"",
			"yourlogin"
			);
	
	public static final BooleanParameter PARAM_SERVER_DISABLE_SSL = new BooleanParameter(
			"server_disable_ssl", 
			"disable SSL check", 
			"useful when certificate issues like self-certification", 
			false
			);
	
	public static final BooleanParameter PARAM_PROXY_ENABLED  = new BooleanParameter(
			"proxy_enabled", 
			"use proxy", 
			"should a proxy be used to connect the server and run the model", 
			false
			);
	
	public static final StringParameter PARAM_PROXY_HOST = new StringParameter(
			"proxy_host", 
			"proxy host", 
			"for instance myproxy.edf.fr",
			"pcyvipncp2n.edf.fr"
			);
	
	public static final IntParameter PARAM_PROXY_PORT = new IntParameter(
			"proxy_port", 
			"proxy port", 
			"",
			3128,
			0,
			65535
			);
	
	protected List<ModelInput> modelInputs = new LinkedList<>();
	protected List<ModelOutput> modelOutputs = new LinkedList<>();

	public static final IntParameter PARAM_MAX_PARALLEL = new IntParameter(
			"max_parallel", 
			"max simultaneous simulations", 
			"depends on the server characteristics",  
			2,
			1,
			10
			);
	
	
	public AbstractEverestModelAlgo(String name, String description) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.MODELS, 
				null, 
				null
				);
		
		registerParameter(PARAM_MODEL_ID);
		registerParameter(PARAM_SCENARIO_ID);
		
		registerParameter(PARAM_SERVER_URL);
		registerParameter(PARAM_SERVER_URL_DOWNLOAD);
		registerParameter(PARAM_SERVER_LOGIN);
		registerParameter(PARAM_SERVER_PASSWORD);
		registerParameter(PARAM_SERVER_DISABLE_SSL);
		
		registerParameter(PARAM_PROXY_ENABLED);
		registerParameter(PARAM_PROXY_HOST);
		registerParameter(PARAM_PROXY_PORT);
		
		registerParameter(PARAM_MAX_PARALLEL);
		
	}

	public List<ModelInput> getEverestModelInputs() {
		return Collections.unmodifiableList(this.modelInputs);
	}
	
	public List<ModelOutput> getEverestModelOutputs() {
		return Collections.unmodifiableList(this.modelOutputs);
	}
	
	protected void declareModelInput(ModelInput input) {

		// store this everest model input
		modelInputs.add(input);

		// add these inputs as inputs of our algorithm
		inputs.addAll(input.getGenlabInputs());
		
	}
	
	protected void declareModelOutput(ModelOutput output) {
		
		// store it
		modelOutputs.add(output);
		
		// declare the corresponding output(s) for his algo
		outputs.addAll(output.getGenlabOutputs());
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new EverestModelExec(execution, algoInstance);
	}

}
