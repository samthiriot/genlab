package genlab.graphstream.examples;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.graphstream.algos.generators.BarabasiAlbertAlgo;
import genlab.graphstream.ui.algos.GraphDisplayAlgo;
import genlab.gui.examples.contributors.IGenlabExample;

public final class BAExample1 implements IGenlabExample {

	public BAExample1() {

	}
	

	@Override
	public String getName() {
		return "Barabasi Albert scale-free example";
	}

	@Override
	public String getDescription() {
		return "Shows how a Barabasi-Albert generator can be used with statistical analysis an vizualization of the graph.";
	}
	

	@Override
	public String getFileName() {
		return "gs_barabasi_albert_example_1";
	}



	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		// ref algos
		BarabasiAlbertAlgo ba = new BarabasiAlbertAlgo();
		ConstantValueInteger constantInt = new ConstantValueInteger();
		StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
		GraphDisplayAlgo displayAlgo = new GraphDisplayAlgo();
		
		// create instances inside the workflow
		{	
			IAlgoInstance baInstance = ba.createInstance(workflow);
			workflow.addAlgoInstance(baInstance);
			
			{
				IAlgoInstance constantN = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantN);
				constantN.setValueForParameter(constantInt.getConstantParameter(), 500);
				workflow.connect(
						 constantN,
						 ConstantValueInteger.OUTPUT,
						 baInstance,
						 BarabasiAlbertAlgo.PARAM_N
				);
			}
			
			{
				IAlgoInstance constantM = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantM);
				constantM.setValueForParameter(constantInt.getConstantParameter(), 1);
				workflow.connect(
						constantM,
						ConstantValueInteger.OUTPUT,
						baInstance,
						BarabasiAlbertAlgo.PARAM_M
	
				);
			}
			
			
			IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
			workflow.addAlgoInstance(stdOutInstance);
			workflow.connect(
					baInstance.getOutputInstanceForOutput(BarabasiAlbertAlgo.OUTPUT_GRAPH), 
					stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
					);
			
			IAlgoInstance displayDisplayAlgo = displayAlgo.createInstance(workflow);
			workflow.addAlgoInstance(displayDisplayAlgo);
			workflow.connect(
					baInstance,
					BarabasiAlbertAlgo.OUTPUT_GRAPH,
					displayDisplayAlgo,
					GraphDisplayAlgo.INPUT_GRAPH
					);
		}
		
	}





}
