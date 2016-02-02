package genlab.populations.examples;


import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.ConstantValueString;
import genlab.gui.examples.contributors.AbstractBasicExample;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.populations.algos.AddEmptyAgentsAlgo;
import genlab.populations.algos.CreateEmptyPopulationAlgo;
import genlab.populations.algos.LoadPopulationDescriptionFromFileAlgo;
import genlab.populations.algos.SetAttributeRandomDoubleAlgo;
import genlab.populations.algos.SetAttributeRandomIntegerAlgo;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.persistence.PopulationDescriptionPersistence;

import java.io.File;
import java.util.Collection;
import java.util.Collections;


public class ExampleSimple1 extends AbstractBasicExample {

	public final String FILENAME_RESOURCE_POP_DESC = File.separator+getFileName()+"_population_description.popdesc";
	
	public ExampleSimple1() {

	}

	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
				
		
		// declare the use of the algorithms required for the workflow
		final SetAttributeRandomIntegerAlgo setAttributeRandomIntegerAlgo = new SetAttributeRandomIntegerAlgo();
		final graphicalConsoleAlgo graphicalConsoleAlgo = new GraphicalConsoleAlgo();
		final SetAttributeRandomDoubleAlgo setAttributeRandomDoubleAlgo = new SetAttributeRandomDoubleAlgo();
		final ConstantValueString constantValueString = new ConstantValueString();
		final ConstantValueDouble constantValueDouble = new ConstantValueDouble();
		final AddEmptyAgentsAlgo addEmptyAgentsAlgo = new AddEmptyAgentsAlgo();
		final ConstantValueInteger constantValueInteger = new ConstantValueInteger();
		final LoadPopulationDescriptionFromFileAlgo loadPopulationDescriptionFromFileAlgo = new LoadPopulationDescriptionFromFileAlgo();
		final UpdateAttributesFromBNAlgo updateAttributesFromBNAlgo = new UpdateAttributesFromBNAlgo();
		final GraphDisplay2DAlgo graphDisplay2DAlgo = new GraphDisplay2DAlgo();
		final CreateEmptyPopulationAlgo createEmptyPopulationAlgo = new CreateEmptyPopulationAlgo();
		final WritePopulationForSimdiascaAlgo writePopulationForSimdiascaAlgo = new WritePopulationForSimdiascaAlgo();
		final LoadBayesianNetworkAlgo loadBayesianNetworkAlgo = new LoadBayesianNetworkAlgo();
		
		
		// declare the instances of algorithms in the workflow
		final IAlgoInstance setAttributeRandomIntegerAlgoInstance = setAttributeRandomIntegerAlgo.createInstance(workflow);
		setAttributeRandomIntegerAlgoInstance.setName("rand int attribute 1");
		workflow.addAlgoInstance(setAttributeRandomIntegerAlgoInstance);
		setAttributeRandomIntegerAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.SetAttributeRandomIntegerAlgo._name",
			"rand int attribute 1"
		);
		
		final IAlgoInstance graphicalConsoleAlgoInstance = graphicalConsoleAlgo.createInstance(workflow);
		graphicalConsoleAlgoInstance.setName("graphical console 1");
		workflow.addAlgoInstance(graphicalConsoleAlgoInstance);
		graphicalConsoleAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.gui.algos.GraphicalConsoleAlgo._name",
			"graphical console 1"
		);
		
		final IAlgoInstance setAttributeRandomDoubleAlgoInstance = setAttributeRandomDoubleAlgo.createInstance(workflow);
		setAttributeRandomDoubleAlgoInstance.setName("rand double attribute 1");
		workflow.addAlgoInstance(setAttributeRandomDoubleAlgoInstance);
		setAttributeRandomDoubleAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.SetAttributeRandomDoubleAlgo._name",
			"rand double attribute 1"
		);
		
		final IAlgoInstance constantValueStringInstance = constantValueString.createInstance(workflow);
		constantValueStringInstance.setName("constant string 2");
		workflow.addAlgoInstance(constantValueStringInstance);
		constantValueStringInstance.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"predator"
		);
		constantValueStringInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString_0._name",
			"constant string 2"
		);
		
		final IAlgoInstance constantValueDoubleInstance = constantValueDouble.createInstance(workflow);
		constantValueDoubleInstance.setName("constant double 1");
		workflow.addAlgoInstance(constantValueDoubleInstance);
		constantValueDoubleInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueDouble._name",
			"constant double 1"
		);
		constantValueDoubleInstance.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueDouble.params.value",
			0.0
		);
		
		final IAlgoInstance addEmptyAgentsAlgoInstance = addEmptyAgentsAlgo.createInstance(workflow);
		addEmptyAgentsAlgoInstance.setName("Create empty agents 1");
		workflow.addAlgoInstance(addEmptyAgentsAlgoInstance);
		addEmptyAgentsAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.AddEmptyAgentsAlgo._name",
			"Create empty agents 1"
		);
		
		final IAlgoInstance setAttributeRandomIntegerAlgoInstance1 = setAttributeRandomIntegerAlgo.createInstance(workflow);
		setAttributeRandomIntegerAlgoInstance1.setName("rand int attribute 2");
		workflow.addAlgoInstance(setAttributeRandomIntegerAlgoInstance1);
		setAttributeRandomIntegerAlgoInstance1.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.SetAttributeRandomIntegerAlgo_0._name",
			"rand int attribute 2"
		);
		
		final IAlgoInstance constantValueStringInstance1 = constantValueString.createInstance(workflow);
		constantValueStringInstance1.setName("constant string 1");
		workflow.addAlgoInstance(constantValueStringInstance1);
		constantValueStringInstance1.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"prey"
		);
		constantValueStringInstance1.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString._name",
			"constant string 1"
		);
		
		final IAlgoInstance constantValueIntegerInstance = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance.setName("constant integer 1");
		workflow.addAlgoInstance(constantValueIntegerInstance);
		constantValueIntegerInstance.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			100
		);
		constantValueIntegerInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger._name",
			"constant integer 1"
		);
		
		final IAlgoInstance loadPopulationDescriptionFromFileAlgoInstance = loadPopulationDescriptionFromFileAlgo.createInstance(workflow);
		loadPopulationDescriptionFromFileAlgoInstance.setName("load population description 1");
		workflow.addAlgoInstance(loadPopulationDescriptionFromFileAlgoInstance);
		loadPopulationDescriptionFromFileAlgoInstance.setValueForParameter(
			"param_file",
			new File("/local00/home/B12772/workspaceJunoRCP/runtime-genlab.product/stagePreyPredators/inputs/preys_predators1.popdesc")
		);
		loadPopulationDescriptionFromFileAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.LoadPopulationDescriptionFromFileAlgo._name",
			"load population description 1"
		);
		
		final IAlgoInstance updateAttributesFromBNAlgoInstance = updateAttributesFromBNAlgo.createInstance(workflow);
		updateAttributesFromBNAlgoInstance.setName("updates individuals from BN 1");
		workflow.addAlgoInstance(updateAttributesFromBNAlgoInstance);
		updateAttributesFromBNAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.population.yang.algos.UpdateAttributesFromBNAlgo._name",
			"updates individuals from BN 1"
		);
		
		final IAlgoInstance addEmptyAgentsAlgoInstance1 = addEmptyAgentsAlgo.createInstance(workflow);
		addEmptyAgentsAlgoInstance1.setName("Create empty agents 2");
		workflow.addAlgoInstance(addEmptyAgentsAlgoInstance1);
		addEmptyAgentsAlgoInstance1.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.AddEmptyAgentsAlgo_0._name",
			"Create empty agents 2"
		);
		
		final IAlgoInstance graphDisplay2DAlgoInstance = graphDisplay2DAlgo.createInstance(workflow);
		graphDisplay2DAlgoInstance.setName("2D graph display (graphstream) 1");
		workflow.addAlgoInstance(graphDisplay2DAlgoInstance);
		graphDisplay2DAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.graphstream.ui.algos.GraphDisplay2DAlgo._name",
			"2D graph display (graphstream) 1"
		);
		
		final IAlgoInstance constantValueStringInstance2 = constantValueString.createInstance(workflow);
		constantValueStringInstance2.setName("constant string 6");
		workflow.addAlgoInstance(constantValueStringInstance2);
		constantValueStringInstance2.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"y"
		);
		constantValueStringInstance2.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString_4._name",
			"constant string 6"
		);
		
		final IAlgoInstance constantValueStringInstance3 = constantValueString.createInstance(workflow);
		constantValueStringInstance3.setName("constant string 3");
		workflow.addAlgoInstance(constantValueStringInstance3);
		constantValueStringInstance3.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"spatialEntity"
		);
		constantValueStringInstance3.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString_1._name",
			"constant string 3"
		);
		
		final IAlgoInstance constantValueStringInstance4 = constantValueString.createInstance(workflow);
		constantValueStringInstance4.setName("constant string 4");
		workflow.addAlgoInstance(constantValueStringInstance4);
		constantValueStringInstance4.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"x"
		);
		constantValueStringInstance4.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString_2._name",
			"constant string 4"
		);
		
		final IAlgoInstance constantValueDoubleInstance1 = constantValueDouble.createInstance(workflow);
		constantValueDoubleInstance1.setName("constant double 2");
		workflow.addAlgoInstance(constantValueDoubleInstance1);
		constantValueDoubleInstance1.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueDouble_0._name",
			"constant double 2"
		);
		constantValueDoubleInstance1.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueDouble.params.value",
			359.99
		);
		
		final IAlgoInstance createEmptyPopulationAlgoInstance = createEmptyPopulationAlgo.createInstance(workflow);
		createEmptyPopulationAlgoInstance.setName("empty population 1");
		workflow.addAlgoInstance(createEmptyPopulationAlgoInstance);
		createEmptyPopulationAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.populations.algos.CreateEmptyPopulationAlgo._name",
			"empty population 1"
		);
		
		final IAlgoInstance constantValueStringInstance5 = constantValueString.createInstance(workflow);
		constantValueStringInstance5.setName("constant string 8");
		workflow.addAlgoInstance(constantValueStringInstance5);
		constantValueStringInstance5.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueString.params.value",
			"angle"
		);
		constantValueStringInstance5.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueString_6._name",
			"constant string 8"
		);
		
		final IAlgoInstance constantValueIntegerInstance1 = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance1.setName("width");
		workflow.addAlgoInstance(constantValueIntegerInstance1);
		constantValueIntegerInstance1.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			400
		);
		constantValueIntegerInstance1.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger_2._name",
			"width"
		);
		
		final IAlgoInstance constantValueIntegerInstance2 = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance2.setName("height");
		workflow.addAlgoInstance(constantValueIntegerInstance2);
		constantValueIntegerInstance2.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger_3._name",
			"height"
		);
		constantValueIntegerInstance2.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			300
		);
		
		final IAlgoInstance writePopulationForSimdiascaAlgoInstance = writePopulationForSimdiascaAlgo.createInstance(workflow);
		writePopulationForSimdiascaAlgoInstance.setName("write for Simdisca 1");
		workflow.addAlgoInstance(writePopulationForSimdiascaAlgoInstance);
		writePopulationForSimdiascaAlgoInstance.setValueForParameter(
			"param_file",
			new File("/tmp/simdiasca.export")
		);
		writePopulationForSimdiascaAlgoInstance.setValueForParameter(
			"param_header",
			"%%  generated by GenLab alpha 0.0.1 \n"+
		"\"My Environment\" <- { class_TwoDimensionalEnvironment, ["+width+", "+height+", torus ] }\n"	"
		);
		writePopulationForSimdiascaAlgoInstance.setValueForParameter(
			"param_formatting",
			""\""+({"prey": "Lion", "predator":"Gazelle"}[agent_type])+"-"+id+"\" <- { class_"+({"prey": "Lion", "predator":"Gazelle"}[agent_type])+", [ \""+({"prey": "Simba", "predator":"Grace"}[agent_type])+""
		+id+"\", {"+x+","+y+"}, "+angle+", "+gender+", { user_id, \"My Environment\" }]}"; 
		"
		);
		writePopulationForSimdiascaAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.edf.simdiasca.algos.WritePopulationForSimdiascaAlgo._name",
			"write for Simdisca 1"
		);
		
		final IAlgoInstance constantValueIntegerInstance3 = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance3.setName("constant integer 6");
		workflow.addAlgoInstance(constantValueIntegerInstance3);
		constantValueIntegerInstance3.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			0
		);
		constantValueIntegerInstance3.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger_4._name",
			"constant integer 6"
		);
		
		final IAlgoInstance loadBayesianNetworkAlgoInstance = loadBayesianNetworkAlgo.createInstance(workflow);
		loadBayesianNetworkAlgoInstance.setName("load Bayesian network 1");
		workflow.addAlgoInstance(loadBayesianNetworkAlgoInstance);
		loadBayesianNetworkAlgoInstance.setValueForParameter(
			"param_file",
			new File("/local00/home/B12772/workspaceJunoRCP/runtime-genlab.product/stagePreyPredators/inputs/gender_bn.net")
		);
		loadBayesianNetworkAlgoInstance.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.bayesianinference.smile.algos.LoadBayesianNetworkAlgo._name",
			"load Bayesian network 1"
		);
		
		final IAlgoInstance constantValueIntegerInstance4 = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance4.setName("constant integer 2");
		workflow.addAlgoInstance(constantValueIntegerInstance4);
		constantValueIntegerInstance4.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			200
		);
		constantValueIntegerInstance4.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger_0._name",
			"constant integer 2"
		);
		
		final IAlgoInstance constantValueIntegerInstance5 = constantValueInteger.createInstance(workflow);
		constantValueIntegerInstance5.setName("constant integer 3");
		workflow.addAlgoInstance(constantValueIntegerInstance5);
		constantValueIntegerInstance5.setValueForParameter(
			"genlab.core.model.meta.basics.algos.ConstantValueInteger.params.value",
			0
		);
		constantValueIntegerInstance5.setValueForParameter(
			"genlab.workflow.generate_preys_predators_basic.algos.genlab.core.model.meta.basics.algos.ConstantValueInteger_1._name",
			"constant integer 3"
		);
		
		
		
		// declare connections between algo instances
		workflow.connect(
			loadBayesianNetworkAlgoInstance.getOutputInstanceForOutput("out_bn"),
			updateAttributesFromBNAlgoInstance.getInputInstanceForInput("in_bn")
		);
		workflow.connect(
			constantValueIntegerInstance2.getOutputInstanceForOutput("constantvalue.integer.out"),
			setAttributeRandomIntegerAlgoInstance1.getInputInstanceForInput("in_max")
		);
		workflow.connect(
			constantValueIntegerInstance5.getOutputInstanceForOutput("constantvalue.integer.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_min")
		);
		workflow.connect(
			constantValueStringInstance3.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance1.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			constantValueIntegerInstance2.getOutputInstanceForOutput("constantvalue.integer.out"),
			writePopulationForSimdiascaAlgoInstance.getInputInstanceForInput("in_anything")
		);
		workflow.connect(
			constantValueIntegerInstance4.getOutputInstanceForOutput("constantvalue.integer.out"),
			addEmptyAgentsAlgoInstance1.getInputInstanceForInput("in_count")
		);
		workflow.connect(
			constantValueStringInstance1.getOutputInstanceForOutput("constantvalue.string.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			updateAttributesFromBNAlgoInstance.getOutputInstanceForOutput("out_pop"),
			graphDisplay2DAlgoInstance.getInputInstanceForInput("in_graph")
		);
		workflow.connect(
			constantValueStringInstance3.getOutputInstanceForOutput("constantvalue.string.out"),
			updateAttributesFromBNAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			setAttributeRandomDoubleAlgoInstance.getOutputInstanceForOutput("out_pop"),
			updateAttributesFromBNAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueStringInstance5.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomDoubleAlgoInstance.getInputInstanceForInput("in_attributename")
		);
		workflow.connect(
			addEmptyAgentsAlgoInstance.getOutputInstanceForOutput("out_pop"),
			addEmptyAgentsAlgoInstance1.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			loadPopulationDescriptionFromFileAlgoInstance.getOutputInstanceForOutput("out_pop_desc"),
			createEmptyPopulationAlgoInstance.getInputInstanceForInput("in_pop_desc")
		);
		workflow.connect(
			constantValueStringInstance.getOutputInstanceForOutput("constantvalue.string.out"),
			addEmptyAgentsAlgoInstance1.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			constantValueStringInstance3.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			createEmptyPopulationAlgoInstance.getOutputInstanceForOutput("out_pop"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueStringInstance4.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_attributename")
		);
		workflow.connect(
			constantValueIntegerInstance3.getOutputInstanceForOutput("constantvalue.integer.out"),
			setAttributeRandomIntegerAlgoInstance1.getInputInstanceForInput("in_min")
		);
		workflow.connect(
			addEmptyAgentsAlgoInstance1.getOutputInstanceForOutput("out_pop"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueIntegerInstance.getOutputInstanceForOutput("constantvalue.integer.out"),
			addEmptyAgentsAlgoInstance.getInputInstanceForInput("in_count")
		);
		workflow.connect(
			constantValueIntegerInstance1.getOutputInstanceForOutput("constantvalue.integer.out"),
			setAttributeRandomIntegerAlgoInstance.getInputInstanceForInput("in_max")
		);
		workflow.connect(
			updateAttributesFromBNAlgoInstance.getOutputInstanceForOutput("out_pop"),
			graphicalConsoleAlgoInstance.getInputInstanceForInput("anything")
		);
		workflow.connect(
			constantValueStringInstance3.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomDoubleAlgoInstance.getInputInstanceForInput("in_agenttype")
		);
		workflow.connect(
			constantValueDoubleInstance.getOutputInstanceForOutput("constantvalue.double.out"),
			setAttributeRandomDoubleAlgoInstance.getInputInstanceForInput("in_min")
		);
		workflow.connect(
			constantValueStringInstance2.getOutputInstanceForOutput("constantvalue.string.out"),
			setAttributeRandomIntegerAlgoInstance1.getInputInstanceForInput("in_attributename")
		);
		workflow.connect(
			setAttributeRandomIntegerAlgoInstance1.getOutputInstanceForOutput("out_pop"),
			setAttributeRandomDoubleAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueIntegerInstance1.getOutputInstanceForOutput("constantvalue.integer.out"),
			writePopulationForSimdiascaAlgoInstance.getInputInstanceForInput("in_anything")
		);
		workflow.connect(
			setAttributeRandomIntegerAlgoInstance.getOutputInstanceForOutput("out_pop"),
			setAttributeRandomIntegerAlgoInstance1.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			updateAttributesFromBNAlgoInstance.getOutputInstanceForOutput("out_pop"),
			writePopulationForSimdiascaAlgoInstance.getInputInstanceForInput("in_pop")
		);
		workflow.connect(
			constantValueDoubleInstance1.getOutputInstanceForOutput("constantvalue.double.out"),
			setAttributeRandomDoubleAlgoInstance.getInputInstanceForInput("in_max")
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
		
		AgentType typeSpatialized = new AgentType("spatialized", "describes an agent spatialized");
		typeSpatialized.addAttribute(new Attribute("x",AttributeType.INTEGER));
		typeSpatialized.addAttribute(new Attribute("y",AttributeType.INTEGER));
		
		AgentType typePrey = new AgentType("prey", "describes a prey in a population");
		typePrey.addInheritedTypes(typeSpatialized);
		
		AgentType typePredator = new AgentType("predator", "describes a predator in a population");
		typePredator.addInheritedTypes(typeSpatialized);
		
		PopulationDescription pd = new PopulationDescription();
		pd.addAgentType(typeSpatialized);
		pd.addAgentType(typePrey);
		pd.addAgentType(typePredator);
		
		PopulationDescriptionPersistence.singleton.writeToFile(pd, fPopDesc);
		
	}

	@Override
	public GenlabExampleDifficulty getDifficulty() {
		return GenlabExampleDifficulty.EASY;
	}

	@Override
	public Collection<IFlowType<?>> getIllustratedFlowTypes() {
		return Collections.EMPTY_LIST; // TODO
	}

	@Override
	public Collection<AlgoCategory> getIllustratedAlgoCategories() {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

}
