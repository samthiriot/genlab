package genlab.algog.gui.examples;

import genlab.algog.algos.meta.AbstractGeneAlgo;
import genlab.algog.algos.meta.ECrossoverMethod;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.GoalAlgo;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.gui.jfreechart.algos.AlgoGPlotAlgo;
import genlab.algog.gui.jfreechart.algos.AlgoGPlotRadarAlgo;
import genlab.algog.gui.jfreechart.algos.FirstFront2DAlgo;
import genlab.core.commons.ProgramException;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CreateWorkflowNSGA2FromWorkflow {

	private CreateWorkflowNSGA2FromWorkflow() {
	}
	
	/**
	 * From a workflow, creates a copy of selected algos inside a target algo, 
	 * and returns the map between previous and present algos
	 * @param originalWorkflow
	 * @param selectedAlgos
	 * @param targetWorkflow
	 * @param container
	 * @return
	 */
	protected static Map<IAlgoInstance,IAlgoInstance> copyAlgosFromWorkflowToWorkflow(
			IGenlabWorkflowInstance originalWorkflow,
			Collection<IAlgoInstance> selectedAlgos,
			IGenlabWorkflowInstance targetWorkflow,
			IAlgoContainerInstance container) {
		
		// start by duplicating the algo instances
		Map<IAlgoInstance,IAlgoInstance> original2copy = new HashMap<IAlgoInstance, IAlgoInstance>(selectedAlgos.size());
		
		for (IAlgoInstance ai : selectedAlgos) {
			
			IAlgoInstance resultInstance = ai.cloneInContext(targetWorkflow);
			
			if (container != null) {
				resultInstance.setContainer(container);
				container.addChildren(resultInstance);
			}
			targetWorkflow.addAlgoInstance(resultInstance);
			
			
			original2copy.put(ai, resultInstance);
						
		}
		
		// and add the connections
		for (IAlgoInstance original: original2copy.keySet()) {
			
			// add the input connections
			for (IConnection cInOrig : original.getAllIncomingConnections()) {
				
				IAlgoInstance fromOrigin = cInOrig.getFrom().getAlgoInstance();
				IAlgoInstance fromCopy = original2copy.get(fromOrigin);
				if (fromCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IAlgoInstance toOrigin = cInOrig.getTo().getAlgoInstance();
				IAlgoInstance toCopy = original2copy.get(toOrigin);
				if (toCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IInputOutputInstance outputInstanceCopy = fromCopy.getOutputInstanceForOutput(cInOrig.getFrom().getMeta());
				if (outputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for output "+cInOrig.getFrom().getMeta());
				
				IInputOutputInstance inputInstanceCopy = toCopy.getInputInstanceForInput(cInOrig.getTo().getMeta());
				if (inputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for input "+cInOrig.getTo().getMeta());
				
				IConnection cCopy = targetWorkflow.connect(outputInstanceCopy, inputInstanceCopy);
			
			}

		}
        
		return original2copy;
	}
	
	/**
	 * Among the list of all the referenced algos in Genlab (possibly from plugins),
	 * identify the gene ones
	 * @return
	 */
	private static Collection<AbstractGeneAlgo> listExistingGeneAlgos() {
		
		Collection<AbstractGeneAlgo> res = new LinkedList<AbstractGeneAlgo>();
		
		for (IAlgo algo : ExistingAlgos.getExistingAlgos().getAlgosForCategory(ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS)) {
			if (algo instanceof AbstractGeneAlgo) {
				res.add((AbstractGeneAlgo)algo);
			}
		}
		
		return res;
	}

	/**
	 * Among the list of all the referenced algos in Genlab (possibly from plugins),
	 * identify the goal ones
	 * @return
	 */
	private static Collection<GoalAlgo> listExistingGoalAlgos() {
		
		Collection<GoalAlgo> res = new LinkedList<GoalAlgo>();
		
		for (IAlgo algo : ExistingAlgos.getExistingAlgos().getAlgosForCategory(ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS)) {
			if (algo instanceof GoalAlgo) {
				res.add((GoalAlgo)algo);
			}
		}
		
		return res;
	}
	
	/**
	 * In the list of gene algos (which might come from an external plugin),
	 * defines the gene algo having an output compliant with this input 
	 * which has the higher priority
	 * @param input
	 * @return
	 */
	private static AbstractGeneAlgo getGeneAlgoForInput(IInputOutput<?> input) {
		
		AbstractGeneAlgo foundAlgo = null;
		Integer bestPriority = Integer.MIN_VALUE;
		
		final Collection<AbstractGeneAlgo> candidateAlgos = listExistingGeneAlgos();
		
		for (AbstractGeneAlgo algoGene: candidateAlgos) {
			
			if (
					input.getType().compliantWith(algoGene.getMainOutput().getType())
					&&
					algoGene.getPriorityForIntuitiveCreation() > bestPriority
					) {
				foundAlgo = algoGene;
				bestPriority = algoGene.getPriorityForIntuitiveCreation();
			}
		}
		
		return foundAlgo;
		
	}
	
	/**
	 * In the list of goal algos (which might come from an external plugin),
	 * defines the goal algo having an input compliant with this output 
	 * which has the higher priority
	 * @param otuput
	 * @return
	 */
	private static GoalAlgo getGoalAlgoForOutput(IInputOutput<?> output) {
		
		GoalAlgo foundAlgo = null;
		Integer bestPriority = Integer.MIN_VALUE;
		
		final Collection<GoalAlgo> candidateAlgos = listExistingGoalAlgos();
		
		for (GoalAlgo algoGoal: candidateAlgos) {
			
			if (
					algoGoal.INPUT_VALUE.getType().compliantWith(output.getType())
					&&
					algoGoal.getPriorityForIntuitiveCreation() > bestPriority
					) {
				foundAlgo = algoGoal;
				bestPriority = algoGoal.getPriorityForIntuitiveCreation();
			}
		}
		
		return foundAlgo;
		
	}
	
	
	
	public static IGenlabWorkflowInstance createNSGA2InstanceForWorkflow(
			IGenlabWorkflowInstance originalWorkflow,
			Collection<IAlgoInstance> selectedAlgos
			) {
		
		// TODO add parameter so we can say "don't use the current input but do it by hand"
		// TODO add another version from wizard where you say "I want these inputs, not these one..."
		
		// process parameters
		// ... if no algo is selected, they are all selected
		if (selectedAlgos == null) 
			selectedAlgos = new LinkedList<IAlgoInstance>();
		if (selectedAlgos.isEmpty()) {
			selectedAlgos.addAll(originalWorkflow.getAlgoInstances());
		}
		
		// create the target workflow
		final String postfix = " nsga2 "+System.currentTimeMillis();
		IGenlabWorkflowInstance workflowRes = GenlabFactory.createWorkflow(
				originalWorkflow.getProject(),
				originalWorkflow.getName()+postfix, 
				"automatically created for NSGA2", 
				originalWorkflow.getRelativePath()+"/"+originalWorkflow.getFilename()+postfix+".glw"
				);
		
		// inside this workflow, let's create a NSGA2 instance ! 
		IAlgoContainerInstance nsga2instance = null;
		{
			NSGA2GeneticExplorationAlgo algo = new NSGA2GeneticExplorationAlgo();
			nsga2instance = (IAlgoContainerInstance)algo.createInstance(workflowRes);
			nsga2instance.setContainer(workflowRes);
			workflowRes.addAlgoInstance(nsga2instance);
			nsga2instance.setValueForParameter(NSGA2GeneticExplorationAlgo.PARAM_CROSSOVER, ECrossoverMethod.N_POINTS.ordinal());
		}
		
		// copy all the selected elements from the original workflow to the target one
		Map<IAlgoInstance,IAlgoInstance> original2copy = copyAlgosFromWorkflowToWorkflow(
				originalWorkflow, 
				selectedAlgos, 
				workflowRes, 
				nsga2instance
				);

		// add a genome algo instance
		IAlgoInstance genomeInstance = null;
		{
			GenomeAlgo algo = new GenomeAlgo();
			genomeInstance = algo.createInstance(workflowRes);
			
			genomeInstance.setContainer(nsga2instance);
			nsga2instance.addChildren(genomeInstance);
			
			workflowRes.addAlgoInstance(genomeInstance);
			
			
		}
		
		// inputs -> genes
		{
			// list all the candidate inputs, and creates a gene for them
			Collection<IAlgoInstance> createdGenes = new LinkedList<IAlgoInstance>();
			for (IAlgoInstance algoInstOriginal: selectedAlgos) {
			
				IAlgoInstance algoInst = original2copy.get(algoInstOriginal);
				
				for (IInputOutputInstance currentInput: algoInst.getInputInstances()) {
					
					// don't process elements with inputs
					if (!currentInput.getConnections().isEmpty())
						continue;

					// find the gene
					AbstractGeneAlgo algoForGene = getGeneAlgoForInput(currentInput.getMeta());
					if (algoForGene == null) {
						GLLogger.warnUser("unable to find a relevant Gene for input "+currentInput.getMeta().getName()+" of algorithm "+algoInst.getName()+"; you'll have to connect it yourself", CreateWorkflowNSGA2FromWorkflow.class);
						continue;
					}
					
					GLLogger.debugUser("for input  "+currentInput+", will use gene "+algoForGene, CreateWorkflowNSGA2FromWorkflow.class);
					
					// create an instance
					IAlgoInstance geneInstance = algoForGene.createInstance(workflowRes);
					createdGenes.add(geneInstance);
					geneInstance.setContainer(nsga2instance);
					nsga2instance.addChildren(geneInstance);
					workflowRes.addAlgoInstance(geneInstance);
					
					// connect the gene to the input
					workflowRes.connect(
							geneInstance, algoForGene.getMainOutput(),
							algoInst, currentInput.getMeta()
							);
					
					// connect the genome to the gene
					workflowRes.connect(
							genomeInstance, GenomeAlgo.OUTPUT_GENOME,
							geneInstance, AbstractGeneAlgo.INPUT_GENOME
							);
					
				}
			}
	
			// define the mutation probability for genes
			for (IAlgoInstance geneInstance: createdGenes) {
				geneInstance.setValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION, 1d/(double)createdGenes.size());
			}
			
		}
		
		// outputs -> goals
		{
			ConstantValueDouble constantDoubleAlgo = new ConstantValueDouble();
			
			// list all the candidate outputs, and creates outputs for them
			for (IAlgoInstance algoInstOriginal: selectedAlgos) {
			
				IAlgoInstance algoInst = original2copy.get(algoInstOriginal);
				
				for (IInputOutputInstance currentOutput: algoInst.getOutputInstances()) {
					
					if (!currentOutput.getConnections().isEmpty())
						continue;

					// find the gene
					GoalAlgo algoForGene = getGoalAlgoForOutput(currentOutput.getMeta());
					if (algoForGene == null) {
						GLLogger.warnUser("unable to find a relevant Goal for output "+currentOutput.getMeta().getName()+" of algorithm "+algoInst.getName()+"; you'll have to connect it yourself", CreateWorkflowNSGA2FromWorkflow.class);
						continue;
					}
					
					IAlgoInstance goalInstance = null;
					{
						// create an instance
						goalInstance = algoForGene.createInstance(workflowRes);
						goalInstance.setContainer(nsga2instance);
						nsga2instance.addChildren(goalInstance);
						workflowRes.addAlgoInstance(goalInstance);
						
						// connect the goal to the output
						workflowRes.connect(
								algoInst, currentOutput.getMeta(),
								goalInstance, GoalAlgo.INPUT_VALUE
								);
					}
					
					
					{
						// also create an instance of a constant for this goal
						IAlgoInstance constantValueInstance = constantDoubleAlgo.createInstance(workflowRes);
						constantValueInstance.setContainer(nsga2instance);
						nsga2instance.addChildren(constantValueInstance);
						workflowRes.addAlgoInstance(constantValueInstance);
						workflowRes.connect(
								constantValueInstance, constantDoubleAlgo.getConstantOuput(), 
								goalInstance, GoalAlgo.INPUT_TARGET
								);
					}
				}
			}
		}
		
		
		// add the standard analysis and display algorithms

		// add displays
		{
			final AlgoGPlotAlgo algogPlotAlgo = new AlgoGPlotAlgo();
			final IAlgoInstance algogPlotInstance = algogPlotAlgo.createInstance(workflowRes);
			workflowRes.addAlgoInstance(algogPlotInstance);
			algogPlotInstance.setName("Pareto exploration");
			workflowRes.connect(
					nsga2instance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					algogPlotInstance,
					AlgoGPlotAlgo.INPUT_TABLE
					);
		}
		{
			final AlgoGPlotAlgo algogPlotAlgo = new AlgoGPlotAlgo();
			final IAlgoInstance algogPlotInstance = algogPlotAlgo.createInstance(workflowRes);
			workflowRes.addAlgoInstance(algogPlotInstance);
			algogPlotInstance.setName("all exploration");
			workflowRes.connect(
					nsga2instance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE, 
					algogPlotInstance,
					AlgoGPlotAlgo.INPUT_TABLE
					);
		}
		{
			final AlgoGPlotRadarAlgo algoPlotRadarAlgo = new AlgoGPlotRadarAlgo();
			final IAlgoInstance algogPlotInstance = algoPlotRadarAlgo.createInstance(workflowRes);
			workflowRes.addAlgoInstance(algogPlotInstance);
			algogPlotInstance.setName("radar Pareto");

			workflowRes.connect(
					nsga2instance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					algogPlotInstance,
					AlgoGPlotRadarAlgo.INPUT_TABLE
					);
		}
		
		{
			final FirstFront2DAlgo plot2DAlgo = new FirstFront2DAlgo();
			final IAlgoInstance plot2DInstance = plot2DAlgo.createInstance(workflowRes);
			workflowRes.addAlgoInstance(plot2DInstance);
			plot2DInstance.setName("Pareto front");

			plot2DInstance.setName("Pareto fronts");
			workflowRes.connect(
					nsga2instance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					plot2DInstance,
					FirstFront2DAlgo.INPUT_TABLE
					);
		}
		
		
		return workflowRes;
	}

}
