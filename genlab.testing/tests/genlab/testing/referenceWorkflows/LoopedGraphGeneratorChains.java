package genlab.testing.referenceWorkflows;

import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.LoopForAlgo;
import genlab.core.model.meta.basics.algos.AppendToTableAlgo;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.GraphBasicPropertiesAlgo;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.igraph.algos.generation.WattsStrogatzGeneratorAlgo;
import genlab.igraph.algos.measure.IGraphAveragePathLengthAlgo;
import genlab.igraph.algos.measure.IGraphClusteringAlgo;
import genlab.testing.commons.BasicTestWorkflow;

import org.junit.Test;

public class LoopedGraphGeneratorChains {

	@Test
	public void testLoopWithConstantInputsNoReduceWS() {
		
		(new BasicTestWorkflow() {
			
			@Override
			protected void populateWorkflow(IGenlabWorkflowInstance workflow) {
				
				// params
				int N = 500, k=4;
				double p=0.1;
				
				// ref algos
				WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
				ConstantValueInteger constantInt = new ConstantValueInteger();
				ConstantValueDouble constantDouble = new ConstantValueDouble();
				//StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
				LoopForAlgo loopAlgo = new LoopForAlgo();
				
				// create instances inside the workflow
				{	
					IAlgoContainerInstance loopInstnace = (IAlgoContainerInstance)loopAlgo.createInstance(workflow);
					workflow.addAlgoInstance(loopInstnace);
					loopInstnace.setValueForParameter(loopAlgo.PARAM_ITERATIONS, 10);
							
					IAlgoInstance wsInstance = ws.createInstance(workflow);
					workflow.addAlgoInstance(wsInstance);
					wsInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(wsInstance);
				
					{
						IAlgoInstance constantN = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantN);
						constantN.setValueForParameter(constantInt.getConstantParameter(), N);
						workflow.connect(
								 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
						);
					}
					
					{
						IAlgoInstance constantK = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantK);
						constantK.setValueForParameter(constantInt.getConstantParameter(), k);
						workflow.connect(
								constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
						);
					}
					
					{
						IAlgoInstance constantP = constantDouble.createInstance(workflow);
						workflow.addAlgoInstance(constantP);
						constantP.setValueForParameter(constantDouble.getConstantParameter(), p);
						workflow.connect(
								constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
						);
					}
					
					/* TODO display all
					IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
					workflow.addAlgoInstance(stdOutInstance);
					workflow.connect(
							wsInstance.getOutputInstanceForOutput(WattsStrogatzAlgo.OUTPUT_GRAPH), 
							stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
							);
					*/
				}
			}
		}).execAll(
				false,
				false,
				false
				);
		
	}
	


	@Test
	public void testLoopWithConstantInputsWithReduceWS() {
		
		(new BasicTestWorkflow() {
			
			@Override
			protected void populateWorkflow(IGenlabWorkflowInstance workflow) {
				
				// params
				int N = 500, k=4;
				double p=0.1;
				
				// ref algos
				WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
				ConstantValueInteger constantInt = new ConstantValueInteger();
				ConstantValueDouble constantDouble = new ConstantValueDouble();
				LoopForAlgo loopAlgo = new LoopForAlgo();
				AppendToTableAlgo appendTableAlgo = new AppendToTableAlgo();
				GraphBasicPropertiesAlgo graphPropertiesAlgo = new GraphBasicPropertiesAlgo();
				StandardOutputAlgo outputAlgo =  new StandardOutputAlgo();
				
				// create instances inside the workflow
				{	
					IAlgoContainerInstance loopInstnace = (IAlgoContainerInstance)loopAlgo.createInstance(workflow);
					workflow.addAlgoInstance(loopInstnace);
					loopInstnace.setValueForParameter(loopAlgo.PARAM_ITERATIONS, 10);
							
					IAlgoInstance wsInstance = ws.createInstance(workflow);
					workflow.addAlgoInstance(wsInstance);
					wsInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(wsInstance);
					
					IAlgoInstance graphPropertiesInstance = graphPropertiesAlgo.createInstance(workflow);
					workflow.addAlgoInstance(graphPropertiesInstance);
					graphPropertiesInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(graphPropertiesInstance);
				
					workflow.connect(
							wsInstance.getOutputInstanceForOutput(ws.OUTPUT_GRAPH), 
							graphPropertiesInstance.getInputInstanceForInput(graphPropertiesAlgo.INPUT_GRAPH)
							);
					
					{
						IAlgoInstance constantN = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantN);
						constantN.setValueForParameter(constantInt.getConstantParameter(), N);
						workflow.connect(
								 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
						);
					}
					
					{
						IAlgoInstance constantK = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantK);
						constantK.setValueForParameter(constantInt.getConstantParameter(), k);
						workflow.connect(
								constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
						);
					}
					
					{
						IAlgoInstance constantP = constantDouble.createInstance(workflow);
						workflow.addAlgoInstance(constantP);
						constantP.setValueForParameter(constantDouble.getConstantParameter(), p);
						workflow.connect(
								constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
						);
					}
					

					IAlgoInstance appendTableInstance = appendTableAlgo.createInstance(workflow);
					workflow.addAlgoInstance(appendTableInstance);
					workflow.connect(
							graphPropertiesInstance.getOutputInstanceForOutput(GraphBasicPropertiesAlgo.OUTPUT_COUNT_EDGES), 
							appendTableInstance.getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING)
							);
					workflow.connect(
							graphPropertiesInstance.getOutputInstanceForOutput(GraphBasicPropertiesAlgo.OUTPUT_COUNT_VERTICES), 
							appendTableInstance.getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING)
							);
					
					IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
					workflow.addAlgoInstance(stdOutInstance);
					workflow.connect(
							appendTableInstance.getOutputInstanceForOutput(AppendToTableAlgo.OUTPUT_TABLE), 
							stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
							);
					
				}
			}
		}).execAll(
				false,
				false,
				false
				);
		
	}
	
	@Test
	public void testLoopWithConstantInputsWithReduceWSigraph() {
		
		(new BasicTestWorkflow() {
			
			@Override
			protected void populateWorkflow(IGenlabWorkflowInstance workflow) {
				
				// params
				int N = 500, k=4;
				double p=0.1;
				
				// ref algos
				WattsStrogatzGeneratorAlgo ws = new WattsStrogatzGeneratorAlgo();
				ConstantValueInteger constantInt = new ConstantValueInteger();
				ConstantValueDouble constantDouble = new ConstantValueDouble();
				LoopForAlgo loopAlgo = new LoopForAlgo();
				AppendToTableAlgo appendTableAlgo = new AppendToTableAlgo();
				IGraphClusteringAlgo clusteringAlgo = new IGraphClusteringAlgo(); 
				IGraphAveragePathLengthAlgo pathLengthAlgo = new IGraphAveragePathLengthAlgo(); 

				StandardOutputAlgo outputAlgo =  new StandardOutputAlgo();
				
				// create instances inside the workflow
				{	
					IAlgoContainerInstance loopInstnace = (IAlgoContainerInstance)loopAlgo.createInstance(workflow);
					workflow.addAlgoInstance(loopInstnace);
					loopInstnace.setValueForParameter(loopAlgo.PARAM_ITERATIONS, 10);
							
					IAlgoInstance wsInstance = ws.createInstance(workflow);
					workflow.addAlgoInstance(wsInstance);
					wsInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(wsInstance);
					
					IAlgoInstance clusteringInstance = clusteringAlgo.createInstance(workflow);
					workflow.addAlgoInstance(clusteringInstance);
					clusteringInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(clusteringInstance);
				
					workflow.connect(
							wsInstance,
							WattsStrogatzGeneratorAlgo.OUTPUT_GRAPH,
							clusteringInstance,
							IGraphClusteringAlgo.INPUT_GRAPH
							);
				
					IAlgoInstance pathLengthInstance = pathLengthAlgo.createInstance(workflow);
					workflow.addAlgoInstance(pathLengthInstance);
					pathLengthInstance.setContainer(loopInstnace);
					loopInstnace.addChildren(pathLengthInstance);
				
					workflow.connect(
							wsInstance,
							WattsStrogatzGeneratorAlgo.OUTPUT_GRAPH,
							pathLengthInstance,
							IGraphAveragePathLengthAlgo.INPUT_GRAPH
							);
				
					{
						IAlgoInstance constantN = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantN);
						constantN.setValueForParameter(constantInt.getConstantParameter(), N);
						workflow.connect(
								 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzGeneratorAlgo.INPUT_N)
						);
					}
					
					{
						IAlgoInstance constantK = constantInt.createInstance(workflow);
						workflow.addAlgoInstance(constantK);
						constantK.setValueForParameter(constantInt.getConstantParameter(), k);
						workflow.connect(
								constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzGeneratorAlgo.INPUT_NEI)
						);
					}
					
					{
						IAlgoInstance constantP = constantDouble.createInstance(workflow);
						workflow.addAlgoInstance(constantP);
						constantP.setValueForParameter(constantDouble.getConstantParameter(), p);
						workflow.connect(
								constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
								 wsInstance.getInputInstanceForInput(WattsStrogatzGeneratorAlgo.INPUT_P)
						);
					}
					

					IAlgoInstance appendTableInstance = appendTableAlgo.createInstance(workflow);
					workflow.addAlgoInstance(appendTableInstance);
					workflow.connect(
							clusteringInstance,
							IGraphClusteringAlgo.OUTPUT_CLUSTERING_AVERAGE,
							appendTableInstance,
							AppendToTableAlgo.INPUT_ANYTHING
							);
					workflow.connect(
							pathLengthInstance,
							IGraphAveragePathLengthAlgo.OUTPUT_AVERAGE_PATH_LENGTH, 
							appendTableInstance,
							AppendToTableAlgo.INPUT_ANYTHING
							);
					
					IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
					workflow.addAlgoInstance(stdOutInstance);
					workflow.connect(
							appendTableInstance.getOutputInstanceForOutput(AppendToTableAlgo.OUTPUT_TABLE), 
							stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
							);
					
				}
			}
		}).execAll(
				false,
				false,
				false
				);
		
	}


}
