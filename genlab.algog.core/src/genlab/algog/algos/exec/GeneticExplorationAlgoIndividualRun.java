package genlab.algog.algos.exec;

import genlab.algog.algos.meta.AbstractGeneAlgo;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AnIndividual;
import genlab.core.commons.ProgramException;
import genlab.core.exec.ICleanableTask;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A run of only one individual. Contains all the required individuals.
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationAlgoIndividualRun 
									extends AbstractContainerExecution 
									implements ICleanableTask {

	/**
	 * The set of all the algo instance to run for one individual
	 */
	private final Collection<IAlgoInstance> evaluationAlgoInstances;
	
	/**
	 * Associates each gene with the corresponding algorithm instance which created it
	 */
	private final Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance;
	
	/**
	 * The list of values received as inputs
	 */
	private final Map <IConnection,Object> inputConnection2value;
	
	/**
	 * List of the algorithms which evaluate the fitness (that is, the goals)
	 */
	private final List<IAlgoInstance> fitnessAlgoInstances;

	/**
	 * The individual evaluated
	 */
	private final AnIndividual individual;
	
	public GeneticExplorationAlgoIndividualRun(
			// standard genlab stuff
			IExecution exec, 
			IAlgoContainerInstance algoInst,
			Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance,
			Map <IConnection,Object> inputConnection2value,
			Collection<IAlgoInstance> algoInstancesToRun,
			List<IAlgoInstance> fitnessAlgoInstances,
			AnIndividual individual
			) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
				
		this.evaluationAlgoInstances = algoInstancesToRun;
		this.individual = individual;
		this.gene2geneAlgoInstance = gene2geneAlgoInstance;
		this.inputConnection2value = inputConnection2value;
		this.fitnessAlgoInstances = fitnessAlgoInstances;
		
		this.autoUpdateProgressFromChildren = true;
		this.autoFinishWhenChildrenFinished = true;
		
		// we don't want to die if a subprocess has a problem; we will assume the fitness is infinite instead.
		this.ignoreCancelFromChildren = false;
		this.ignoreFailuresFromChildren = false;
		
	}

	
	
	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		// TODO Auto-generated method stub
		super.notifyInputAvailable(to);
	}


	@Override
	protected void hookContainerExecutionFinished(ComputationState state) {
		
		individual.fitness = new Double[fitnessAlgoInstances.size()];
		individual.targets = new Object[fitnessAlgoInstances.size()];
		individual.values = new Object[fitnessAlgoInstances.size()];
		
		if (state == ComputationState.FINISHED_FAILURE || state == ComputationState.FINISHED_CANCEL) {
			// when there is an error, then goal fitness becomes negative infinity.
			messages.warnUser("the evaluation of this individual led to an error. We assume the space of parameters is not full, and a defined Infinite fitness for this individual", getClass());
			
			
			for (int i=0; i<fitnessAlgoInstances.size(); i++) {
				IAlgoInstance ai = fitnessAlgoInstances.get(i);
				IGoalExec goal = (IGoalExec) instance2execForSubtasks.get(ai);

				individual.fitness[i] = StrictMath.pow(10,  14);
				individual.targets[i] = goal.getTarget();
				individual.values[i] = null;

			}
		} else {
			
			for (int i=0; i<fitnessAlgoInstances.size(); i++) {
				
				IAlgoInstance ai = fitnessAlgoInstances.get(i);
				IGoalExec goal = (IGoalExec) instance2execForSubtasks.get(ai);
				
				individual.fitness[i] = goal.getFitness();
				individual.targets[i] = goal.getTarget();
				individual.values[i] = goal.getActualValue();
				
				if (individual.fitness == null)
					throw new ProgramException("the fitness should never be empty when the execution is finished");
				
			}
		}

		messages.debugTech("received fitness for "+individual+" => "+individual.fitnessToString(), getClass());
	}
	

	
	@Override
	public void initInputs(Map<IAlgoInstance, IAlgoExecution> instance2exec) {

		// don't. 
		// super.initInputs(instance2exec);
		
		// also, we store the table for later usage (when we will create the subtasks !)
		this.instance2execOriginal = instance2exec;
				
		
		// and we create a version to be transmitted to our subtasks
		instance2execForSubtasks = initCreateExecutionsForSubAlgos(evaluationAlgoInstances);
		//new HashMap<IAlgoInstance, IAlgoExecution>(instance2execOriginal);
		/*
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {
			// for each algo exec out of this container, the actual 
			// contact during exec will be the supervisor.
			instance2execForSubtasks.put(c.getFrom().getAlgoInstance(), this);	
		}//return individual.genome.getGenes()[i];
		*/

		// prepare the mapping
		// each gene is mapped with ME
		for (IAlgoInstance sub: evaluationAlgoInstances) {
			for (IConnection cIn: sub.getAllIncomingConnections()) {
				
				if (cIn.getFrom().getAlgoInstance().getAlgo() instanceof AbstractGeneAlgo) {
					
					// genes act as input of several children algos
					// they should refer to ME to retrieve these values !
					instance2execForSubtasks.put(cIn.getFrom().getAlgoInstance(), this);
				}
					
			}
		}
		
		
		initContainerAlgosChildren(evaluationAlgoInstances, instance2execForSubtasks);
		
		initParentForSubtasks(evaluationAlgoInstances, instance2execForSubtasks);
		
		// special case of container algos: for each container exec, assume it represents the 
		// exec instance for each children.
		// so all the algos interested in the results of containers' child
		// will actually listen for the container
		initContainerAlgosChildren(evaluationAlgoInstances, instance2execForSubtasks);
		initLinksWithSubExec(instance2execForSubtasks);
		
		initAddTasksAsSubtasks(evaluationAlgoInstances, instance2execForSubtasks);
		
	
		progress.setComputationState(ComputationState.READY);
	}


	protected Object getValueForGene(IAlgoInstance ai) {
		
		AGene<?>[] genomeGenes = individual.genome.getGenes();
		for (int i=0; i<genomeGenes.length; i++) {
			AGene<?> gene = genomeGenes[i];
			IAlgoInstance aiCurrent = gene2geneAlgoInstance.get(gene);
			if (aiCurrent == ai)
				return individual.genes[i];
		}
		return null;
	}
	
	protected IConnectionExecution getExecutableConnectionFor(IConnection cIn) {
		IAlgoExecution toEx = instance2execForSubtasks.get(cIn.getTo().getAlgoInstance());
		if (toEx == null)
			throw new ProgramException("unable to find executable connection for "+cIn);
		Collection<IConnectionExecution> toExCs = toEx.getConnectionsForInput(cIn.getTo());
		for (IConnectionExecution cExCurrent : toExCs) {
			if (cExCurrent.getConnection().equals(cIn)) {
				return cExCurrent;
			}
		}
		return null;
	}

	@Override
	public void run() {
	
		progress.setComputationState(ComputationState.STARTED);
		setResult(new ComputationResult(algoInst, progress, messages));
		
		// set the initial values of each child (for values coming from outside) 
		messages.traceTech("forcing input values...", getClass());
		for (Map.Entry<IConnection,Object> connection2value: inputConnection2value.entrySet()) {
			
			IConnectionExecution cEx = getExecutableConnectionFor(connection2value.getKey());
			Object valueFromOuside = connection2value.getValue();
			cEx.forceValue(valueFromOuside);
		}
		
		// set the initial values of each child (for gene values)
		for (IAlgoInstance sub: evaluationAlgoInstances) {
			messages.traceTech("defining gene values for sub algo "+sub.getName(), getClass());
			for (IConnection cIn: sub.getAllIncomingConnections()) {
				
				if (cIn.getFrom().getAlgoInstance().getAlgo() instanceof AbstractGeneAlgo) {

					IConnectionExecution cEx = getExecutableConnectionFor(cIn);
					
					Object valueForGene = getValueForGene(cIn.getFrom().getAlgoInstance());
					
					cEx.forceValue(valueForGene);
					
				}
					
			}
		}
		
		
	}

	public final AnIndividual getIndividual() {
		return individual; 
	}
	
	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(individual.genome.name).append("] ");
		sb.append(individual.id);
		return sb.toString();
	}
	
	@Override
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("- genome: ").append(individual.genome.name).append("\n");
		sb.append("- id: ").append(individual.id).append("\n");
		AGene<?>[] genes = individual.genome.getGenes();
		for (int i=0; i<genes.length; i++) {
			sb.append("- ").append(genes[i].name).append(": ").append(individual.genes[i]).append("\n");
		}
		
		return sb.toString();
	}

}
