package genlab.populations.algos;

import genlab.arithmetics.ParserFactory;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.StringFlowType;
import genlab.core.parameters.TextParameter;
import genlab.populations.bo.IPopulation;
import genlab.populations.execs.SetAttributeArithmeticsExec;
import genlab.populations.flowtypes.PopulationFlowType;

public class SetAttributeArithmeticsAlgo extends BasicAlgo {


	public static final InputOutput<IPopulation> INPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"in_pop", 
			"population", 
			"the population to fill"
			);
	
	public static final InputOutput<String> INPUT_TYPENAME = new InputOutput<String>(
			StringFlowType.SINGLETON, 
			"in_agenttype", 
			"agent type", 
			"type of agents to create"
			);
	

	public static final InputOutput<String> INPUT_ATTRIBUTENAME = new InputOutput<String>(
			StringFlowType.SINGLETON, 
			"in_attributename", 
			"attribute name", 
			"the name of the attribute to generate randomly"
			);
	
	public static final InputOutput<IPopulation> OUTPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"out_pop", 
			"population", 
			"the population filled"
			);
	
	public static final String PARAM_MATH_NAME = "param_math";

	public static final TextParameter PARAM_FORMULA = new TextParameter(
			"param_math", 
			"formula", 
			"the formula to compute the value of the attribute.\n"
			+ "Syntax: "+ParserFactory.getDefaultExpressionParser().getAllPossibleSyntaxesShort()+"\n"
			+ "You can also use any attribute id as a variable name.",
			"x + 2"
			);
	
	public SetAttributeArithmeticsAlgo() {
		super(
				"attributes from math", 
				"updates the value of attributes based on some mathematical computations", 
				ExistingAlgoCategories.GENERATORS_POPULATIONS, 
				null, 
				null
				);
		
		inputs.add(INPUT_POPULATION);
		inputs.add(INPUT_TYPENAME);
		inputs.add(INPUT_ATTRIBUTENAME);
		
		outputs.add(OUTPUT_POPULATION);
		
		registerParameter(PARAM_FORMULA);
	}


	
	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new SetAttributeArithmeticsExec(execution, algoInstance);
	}

}
