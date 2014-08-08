package genlab.bayesianinference.smile.algos;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.meta.BayesianNetworkFlowType;
import genlab.bayesianinference.smile.exec.LoadBayesianNetworkFromFileExec;
import genlab.core.commons.FileUtils;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.parameters.FileParameter;

public class LoadBayesianNetworkAlgo extends BasicAlgo {


	public static final FileParameter PARAM_FILE = new FileParameter(
			"param_file", 
			"file", 
			"the file into which write the result", 
			FileUtils.getHomeDirectoryFile()
			);
	
	public static final InputOutput<IBayesianNetwork> OUTPUT_BN = new InputOutput<IBayesianNetwork>(
			BayesianNetworkFlowType.SINGLETON, 
			"out_bn", 
			"BN", 
			"the Bayesian network loaded from the file"
	);
	
	public LoadBayesianNetworkAlgo() {
		super(
				"load Bayesian network", 
				"read a Bayesian network from file", 
				ExistingAlgoCategories.PARSER, 
				null, 
				null
				);

		registerParameter(PARAM_FILE);
		
		outputs.add(OUTPUT_BN);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new LoadBayesianNetworkFromFileExec(
				execution, 
				algoInstance
				);
	}

}
