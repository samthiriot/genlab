package genlab.populations.examples;

import java.io.File;
import java.io.ObjectInputStream.GetField;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueString;
import genlab.gui.examples.contributors.AbstractBasicExample;
import genlab.gui.examples.contributors.IGenlabExample;
import genlab.populations.algos.CreateEmptyPopulationAlgo;
import genlab.populations.algos.LoadPopulationDescriptionFromFileAlgo;
import genlab.populations.algos.SetAttributeRandomIntegerAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.persistence.PopulationDescriptionConverter;
import genlab.populations.persistence.PopulationDescriptionPersistence;

public class ExampleSimple1 extends AbstractBasicExample {

	public final String FILENAME_RESOURCE_POP_DESC = File.separator+getFileName()+"_population_description.popdesc";
	
	public ExampleSimple1() {

	}

	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		
		IAlgoInstance loadPopDescInstance = (new LoadPopulationDescriptionFromFileAlgo()).createInstance(workflow);
		workflow.addAlgoInstance(loadPopDescInstance);
		loadPopDescInstance.setName("load popdesc");
		loadPopDescInstance.setValueForParameter(
				LoadPopulationDescriptionFromFileAlgo.PARAMETER_FILE, 
				new File(FILENAME_RESOURCE_POP_DESC)
				);
		
		IAlgoInstance createEmptyInstance = (new CreateEmptyPopulationAlgo()).createInstance(workflow);
		workflow.addAlgoInstance(createEmptyInstance);
		createEmptyInstance.setName("create empty population");
		
		workflow.connect(
				loadPopDescInstance, 
				LoadPopulationDescriptionFromFileAlgo.OUTPUT_POPULATION_DESCRIPTION, 
				createEmptyInstance, 
				CreateEmptyPopulationAlgo.INPUT_POPULATION_DESCRIPTION
				);
		
		final SetAttributeRandomIntegerAlgo setAttributeRandomIntegerAlgo = new SetAttributeRandomIntegerAlgo();
		
		IAlgoInstance setAttributeRandomIntegerInstance = setAttributeRandomIntegerAlgo.createInstance(workflow);
		workflow.addAlgoInstance(setAttributeRandomIntegerInstance);
		setAttributeRandomIntegerInstance.setValueForParameter(
				SetAttributeRandomIntegerAlgo.PARAM_MIN,
				0
				);
		setAttributeRandomIntegerInstance.setValueForParameter(
				SetAttributeRandomIntegerAlgo.PARAM_MAX,
				100
				);
		
		createAndLinkConstantString(
				workflow, 
				"x", 
				setAttributeRandomIntegerInstance, 
				SetAttributeRandomIntegerAlgo.INPUT_ATTRIBUTENAME
				);
		createAndLinkConstantString(
				workflow, 
				"prey", 
				setAttributeRandomIntegerInstance, 
				SetAttributeRandomIntegerAlgo.INPUT_TYPENAME
				);
		workflow.connect(
				createEmptyInstance, 
				CreateEmptyPopulationAlgo.OUTPUT_POPULATION,
				setAttributeRandomIntegerInstance,
				SetAttributeRandomIntegerAlgo.INPUT_POPULATION
				);
		
		
	}

	@Override
	public String getFileName() {
		return "create_simple_preys";
	}

	@Override
	public String getName() {
		return "create simple preys";
	}

	@Override
	public String getDescription() {
		return "creates a simple population with only one type of agent (preys) spatialized in a 2D discrete environment; coordinates are random";
	}

	@Override
	public void createFiles(File resourcesDirectory) {
		
		File fPopDesc = new File(resourcesDirectory, FILENAME_RESOURCE_POP_DESC);
		
		AgentType typePrey = new AgentType("prey", "describes a prey in a population");
		typePrey.addAttribute(new Attribute("x",AttributeType.INTEGER));
		typePrey.addAttribute(new Attribute("y",AttributeType.INTEGER));
		
		PopulationDescription pd = new PopulationDescription();
		pd.addAgentType(typePrey);
		
		PopulationDescriptionPersistence.singleton.writeToFile(pd, fPopDesc);
		
	}

}
