package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.commons.ProgramException;
import genlab.core.exec.ICleanableTask;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The executable task which evaluates one complete generation of a genetic algorithm.
 * Will creates and supervize the subtasks corresponding to the evaluation of each individual.
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationOneGeneration 
								extends AbstractContainerExecutionSupervisor 
								implements ICleanableTask {

	private GeneticExplorationAlgoContainerInstance geneAlgoInst;
	
	private final String name;
	
	private final Map<AGenome,Set<AnIndividual>> generationToEvaluate;
	private Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance;
	private final Map<AGenome, List<IAlgoInstance>> genome2fitnessOutput;
	private final Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance;
	
	private int totalIterationsToDo = 0;
	private int totalIterationsSubmitted = 0;

	/**
	 * Stores the iterator over genomes, the place for the next computation
	 */
	private final Object lockerParameters = new Object();
	private Iterator<AGenome> currentComputationIteratorProcessedGenome = null;
	private AGenome currentGenome = null;
	private Iterator<AnIndividual> currentIteratorIndividual;
	private AnIndividual currentIndividual = null;

	private final Object lockerResults = new Object();
	private Set<AnIndividual> computedIndividuals = new HashSet<AnIndividual>();

	
	private final Map <IConnection,Object> inputConnection2value;
	
	/**
	 * 
	 * @param exec the execution context
	 * @param algoInst the genetic algo instance 
	 * @param generationToEvaluate the generation to be evaluated in this iteration
	 * @param genome2algoInstance the set of algo instances to use for the computation
	 */
	public GeneticExplorationOneGeneration(
			IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst,
			Map<AGenome,Set<AnIndividual>> generationToEvaluate,
			Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance,
			Map<AGenome, List<IAlgoInstance>> map,
			Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance,
			Map <IConnection,Object> inputConnection2value,
			String name
			) {
		
		super(exec, algoInst);
		
		this.geneAlgoInst = algoInst;
		this.name = name;
		
		this.generationToEvaluate = generationToEvaluate;
		this.genome2fitnessOutput = map;
		this.genome2algoInstance = genome2algoInstance;
		this.gene2geneAlgoInstance = gene2geneAlgoInstance;
		this.inputConnection2value = inputConnection2value;
				
		this.autoUpdateProgressFromChildren = false;
		// we don't want to die if a subprocess has a problem; we will assume the fitness is infinite instead.
		this.ignoreCancelFromChildren = true;
		this.ignoreFailuresFromChildren = true;
	}

	@Override
	protected void initFirstRun() {
		
		messages.debugTech("should create the exec for this generation", getClass());
		
		// compute the count of iterations to do
		totalIterationsToDo = 0;
		for (Set<AnIndividual> popToCompute : generationToEvaluate.values()) {
			totalIterationsToDo += popToCompute.size();
		}
		progress.setProgressTotal(totalIterationsToDo);
		
		messages.debugTech("this generation requires "+totalIterationsToDo+" iterations", getClass());
		totalIterationsSubmitted = 0;
		
		
	}

	@Override
	protected void startOfIteration() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean shouldContinueRun() {
		return totalIterationsSubmitted<totalIterationsToDo;
	}

	@Override
	protected void endOfRun() {
		// TODO Auto-generated method stub

	}
	

	
	@Override
	protected IAlgoExecution createNextExecutionForOneIteration() {
		
		GeneticExplorationAlgoIndividualRun resExec;
		
		synchronized (lockerParameters) {
		
			// shit to the next individual to evaluate
			nextIteration();

			// create exec
			messages.traceTech("creating the executable for the exploration of individual: "+currentIndividual+" of genome "+currentGenome, getClass());
			Collection<IAlgoInstance> algoInstancesToRun = genome2algoInstance.get(currentGenome);
			List<IAlgoInstance> fitnessOutput = genome2fitnessOutput.get(currentGenome);
			
			resExec = new GeneticExplorationAlgoIndividualRun(
					exec, 
					geneAlgoInst, 
					gene2geneAlgoInstance,
					inputConnection2value,
					algoInstancesToRun,
					fitnessOutput,
					currentIndividual
					);	
			resExec.setParent(this);
			//addTask(resExec);
			
			
			// init its links
			messages.traceTech("init links", getClass());
			resExec.initInputs(instance2execForSubtasks);
		}
		
		return resExec;
		
	}
	
	/**
	 * Shifts to the next iteration. After this call, {@link #currentGenome} and {@link #currentIteratorIndividual}
	 * will be set to valid values.
	 */
	protected void nextIteration() {
		
	
		// initial case: retrieve the first genome
		if (currentComputationIteratorProcessedGenome == null) {
			// init
			currentComputationIteratorProcessedGenome = generationToEvaluate.keySet().iterator();
			currentGenome = currentComputationIteratorProcessedGenome.next();
			currentIteratorIndividual = generationToEvaluate.get(currentGenome).iterator();
		} else {
		
			// shift to the next individual
//			currentIteratorIndividual.next();
		}
		
		// retrieve current population
//		Set<AnIndividual> currentPopulation = generationToEvaluate.get(currentGenome);
		
		// if we finished the current population, then shift to the next genome !
		if (!currentIteratorIndividual.hasNext()) {
			currentGenome = currentComputationIteratorProcessedGenome.next();
			currentIteratorIndividual = generationToEvaluate.get(currentGenome).iterator();
//			currentPopulation = generationToEvaluate.get(currentGenome);
		}
		
		currentIndividual = currentIteratorIndividual.next();
		
		totalIterationsSubmitted++;
	}
	
	protected void computeResultsForIndividual(GeneticExplorationAlgoIndividualRun indivRun) {

		AnIndividual indiv = indivRun.getIndividual();

		// store results
		synchronized (lockerResults) {
			
			this.computedIndividuals.add(indiv);
			/*if (!this.computedIndividuals.add(indiv)) {
				messages.warnTech("we received an individual which was already evaluated: "+indiv, getClass());
			}*/

		}
		
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (!progress.getComputationState().isFinished())
			return; // don't care about non finished events 
		
		// one of our child finished
				
		// ignore progress which is not coming from my children
		if (!tasks.contains(progress.getAlgoExecution()))
			return; 

		// retrieve the result for this computation
		if (!(progress.getAlgoExecution() instanceof GeneticExplorationAlgoIndividualRun))
			throw new ProgramException("should not receive messages from other than individual tests");
		
		// retrieve results
		GeneticExplorationAlgoIndividualRun indivRun = (GeneticExplorationAlgoIndividualRun)progress.getAlgoExecution();
				
		// process them
		computeResultsForIndividual(indivRun);
		
		// update our progress
		progress.incProgressMade();
		
		//messages.infoTech("computed "+computedIndividuals.size()+" over "+totalIterationsToDo, getClass());
		if (computedIndividuals.size() == totalIterationsToDo) {

			//messages.traceTech("all subs terminated; should transmit results", getClass());
			this.progress.setComputationState(ComputationState.FINISHED_OK);
		}
		
		// do not enter the complicated generic management in our simple case !
		// super.computationStateChanged(progress);
	}


	@Override
	protected String getSuffixForCurrentIteration() {
		return " "+totalIterationsSubmitted+"/"+totalIterationsToDo+" ("+currentGenome+" ind "+currentIndividual+")";
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a copy of the computed individuals
	 * @return
	 */
	public Set<AnIndividual> getComputedIndividuals() {
		// store results
		synchronized (lockerResults) {
			
			if (!progress.getComputationState().isFinished())
				throw new ProgramException("asked for a fitness for an exploration which did not terminated yet");
				
				
			return Collections.unmodifiableSet(this.computedIndividuals);
		}
	}
	
//	/**
//	 * Returns a copy of the computed fitness for each individual of the generation.
//	 * @return
//	 */
//	public Map<AnIndividual,Double[]> getComputedFitness() {
//		// store results
//		synchronized (lockerResults) {
//			
//			if (!progress.getComputationState().isFinished())
//				throw new ProgramException("asked for a fitness for an exploration which did not terminated yet");
//				
//				
//			return new HashMap<AnIndividual, Double[]>(computedFitness);
//		}
//	}
//	
//	/**
//	 * Returns a copy of the computed targets for each individual of the generation.
//	 * @return
//	 */
//	public Map<AnIndividual,Object[]> getComputedTargets() {
//		// store results
//		synchronized (lockerResults) {
//			
//			if (!progress.getComputationState().isFinished())
//				throw new ProgramException("asked for a fitness for an exploration which did not terminated yet");
//				
//				
//			return new HashMap<AnIndividual, Object[]>(computedTargets);
//		}
//	}
//	
//	/**
//	 * Returns a copy of the computed targets for each individual of the generation.
//	 * @return
//	 */
//	public Map<AnIndividual,Object[]> getComputedValues() {
//		// store results
//		synchronized (lockerResults) {
//			
//			if (!progress.getComputationState().isFinished())
//				throw new ProgramException("asked for a fitness for an exploration which did not terminated yet");
//				
//				
//			return new HashMap<AnIndividual, Object[]>(computedValues);
//		}
//	}
	
	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {

		// we have no input of ourselves
		
		// also, we store the table for later usage (when we will create the subtasks !)
		
		this.instance2execOriginal = instance2exec;
		
		// and we create a version to be transmitted to our subtasks
		instance2execForSubtasks = new HashMap<IAlgoInstance, IAlgoExecution>(instance2execOriginal);
		/*
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			// for each algo exec out of this container, the actual 
			// contact during exec will be the supervisor.
			instance2execForSubtasks.put(c.getFrom().getAlgoInstance(), this);	
		}
		*/
		
		progress.setComputationState(ComputationState.READY);
	}
	

	@Override
	public void run() {
		super.run();
		progress.setProgressTotal(totalIterationsToDo);
		
	}
}
