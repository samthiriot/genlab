package genlab.populations.examples;

import java.io.File;
import java.io.ObjectInputStream.GetField;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.gui.examples.contributors.AbstractBasicExample;
import genlab.core.model.meta.basics.algos.ConstantValueString;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.populations.algos.AddEmptyAgentsAlgo;
import genlab.populations.algos.CreateEmptyPopulationAlgo;
import genlab.populations.algos.LoadPopulationDescriptionFromFileAlgo;
import genlab.populations.algos.SetAttributeRandomIntegerAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.persistence.PopulationDescriptionPersistence;

public class ExampleSimple1 extends AbstractBasicExample {

	public final String FILENAME_RESOURCE_POP_DESC = File.separator+getFileName()+"_population_description.popdesc";
	
	public ExampleSimple1() {

	}

	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		

		// declare the use of the algorithms required for the workflow
		final ConstantValueString constantValueString = new ConstantValueString();
		final ConstantValueInteger constantValueInteger = new ConstantValueInteger();
		final AddEmptyAgentsAlgo addEmptyAgentsAlgo = new AddEmptyAgentsAlgo();
		final CreateEmptyPopulationAlgo createEmptyPopulationAlgo = new CreateEmptyPopulationAlgo();
		final LoadPopulationDescriptionFromFileAlgo loadPopulationDescriptionFromFileAlgo = new LoadPopulationDescriptionFromFileAlgo();
		final SetAttributeRandomIntegerAlgo setAttributeRandomIntegerAlgo = new SetAttributeRandomIntegerAlgo();


		// declare the instances of algorithms in the workflow
		//final IAlgoInstance graphicalConsoleAlgoInstance = graphicalConsoleAlgo.createInstance(workflow);
		//workflow.addAlgoInstance(graphicalConsoleAlgoInstance);

		final IAlgoInstance constantValueStringInstance = constantValueString.createInstance(workflow);
		workflow.addAlgoInstance(constantValueStringInstance);
		constantValueStringInstance.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"x"
		);

		final IAlgoInstance constantValueStringInstance1 = constantValueString.createInstance(workflow);
		workflow.addAlgoInstance(constantValueStringInstance1);
		constantValueStringInstance1.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"prey"
		);

		final IAlgoInstance constantValueIntegerInstance = constantValueInteger.createInstance(workflow);
		workflow.addAlgoInstance(constantValueIntegerInstance);
		constantValueIntegerInstance.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			20
		);

		final IAlgoInstance addEmptyAgentsAlgoInstance = addEmptyAgentsAlgo.createInstance(workflow);
		workflow.addAlgoInstance(addEmptyAgentsAlgoInstance);

		final IAlgoInstance constantValueIntegerInstance1 = constantValueInteger.createInstance(workflow);
		workflow.addAlgoInstance(constantValueIntegerInstance1);
		constantValueIntegerInstance1.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			20
		);

		final IAlgoInstance createEmptyPopulationAlgoInstance = createEmptyPopulationAlgo.createInstance(workflow);
		workflow.addAlgoInstance(createEmptyPopulationAlgoInstance);

		final IAlgoInstance loadPopulationDescriptionFromFileAlgoInstance = loadPopulationDescriptionFromFileAlgo.createInstance(workflow);
		workflow.addAlgoInstance(loadPopulationDescriptionFromFileAlgoInstance);
		loadPopulationDescriptionFromFileAlgoInstance.setValueForParameter(
			"param_file",
			new File("data/create_simple_preys/create_simple_preys_population_description.popdesc")
		);

		final IAlgoInstance addEmptyAgentsAlgoInstance1 = addEmptyAgentsAlgo.createInstance(workflow);
		workflow.addAlgoInstance(addEmptyAgentsAlgoInstance1);

		final IAlgoInstance constantValueStringInstance2 = constantValueString.createInstance(workflow);
		workflow.addAlgoInstance(constantValueStringInstance2);
		constantValueStringInstance2.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"prey"
		);

		final IAlgoInstance setAttributeRandomIntegerAlgoInstance = setAttributeRandomIntegerAlgo.createInstance(workflow);
		workflow.addAlgoInstance(setAttributeRandomIntegerAlgoInstance);
		setAttributeRandomIntegerAlgoInstance.setValueForParameter(
			"param_min",
			0
		);
		setAttributeRandomIntegerAlgoInstance.setValueForParameter(
			"param_max",
			100
		);

		final IAlgoInstance constantValueStringInstance3 = constantValueString.createInstance(workflow);
		workflow.addAlgoInstance(constantValueStringInstance3);
		constantValueStringInstance3.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"predators"
		);



		// declare connections between algo instances
		workflow.connect(
			addEmptyAgentsAlgoInstance.getOutputInstanceForOutput("out_pop"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueStringInstance.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_attributename")
		);
		workflow.connect(
			constantValueIntegerInstance.getOutputInstanceForOutput("constantvalue.integer.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_count")
		);
		/*workflow.connect(
			setAttributeRandomIntegerAlgoInstance.getOutputInstanceForOutput("out_pop"),
			graphicalConsoleAlgoInstance.getInputInstanceForInput("anything")
		);*/
		workflow.connect(
			constantValueIntegerInstance.getOutputInstanceForOutput("constantvalue.integer.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_count")
		);
		workflow.connect(
			constantValueStringInstance.getOutputInstanceForOutput("constantvalue.string.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			addEmptyAgentsAlgoInstance.getOutputInstanceForOutput("out_pop"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			loadPopulationDescriptionFromFileAlgoInstance.getOutputInstanceForOutput("out_pop_desc"),
			createEmptyPopulationAlgoInstance.getInputInstanceForInput("in_pop_desc")
		);
		workflow.connect(
			constantValueStringInstance.getOutputInstanceForOutput("constantvalue.string.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			createEmptyPopulationAlgoInstance.getOutputInstanceForOutput("out_pop"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueStringInstance.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_agenttype")
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
