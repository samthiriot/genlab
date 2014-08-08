package genlab.populations.algos;

import genlab.core.commons.FileUtils;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.parameters.FileParameter;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.execs.LoadPopulationDescriptionFromFileExec;
import genlab.populations.flowtypes.PopulationDescriptionFlowType;

public class LoadPopulationDescriptionFromFileAlgo extends BasicAlgo {

	public static final FileParameter PARAMETER_FILE = new FileParameter(
			"param_file", 
			"file", 
			"the file to parse", 
			FileUtils.getHomeDirectoryFile()
			);
	

	public static final InputOutput<PopulationDescription> OUTPUT_POPULATION_DESCRIPTION = new InputOutput<PopulationDescription>(
			PopulationDescriptionFlowType.SINGLETON, 
			"out_pop_desc", 
			"pop desc", 
			"population description"
			);
	
	public LoadPopulationDescriptionFromFileAlgo() {
		super(
				"load population description", 
				"reads from file a population description", 
				ExistingAlgoCategories.PARSER_POPULATION, 
				null, 
				null
				);

		registerParameter(PARAMETER_FILE);

		outputs.add(OUTPUT_POPULATION_DESCRIPTION);
		
	}

	public LoadPopulationDescriptionFromFileAlgo(String name, String description,
			String categoryId) {
		super(name, description, categoryId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new LoadPopulationDescriptionFromFileExec(execution, algoInstance);
	}

}
