package genlab.algog.algos.exec;

import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ConnectionExec;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.exec.IComputationResult;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A run of only one individual. Contains all the required individuals.
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationAlgoIndividualRun extends AbstractContainerExecution {

	private final Collection<IAlgoInstance> algoInstancesToRun;
	private final int individualId;
	private final Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance;
	private final Map <IConnection,Object> inputConnection2value;
	private final IInputOutputInstance fitnessOutput;
	private Double resultFitness = null;
	private final AnIndividual individual;
	
	public GeneticExplorationAlgoIndividualRun(
			// standard genlab stuff
			IExecution exec, 
			IAlgoContainerInstance algoInst,
			Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance,
			Map <IConnection,Object> inputConnection2value,
			Collection<IAlgoInstance> algoInstancesToRun,
			IInputOutputInstance fitnessOutput,
			AnIndividual individual,
			int individualId
			) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
				
		this.algoInstancesToRun = algoInstancesToRun;
		this.individualId = individualId;
		this.individual = individual;
		this.gene2geneAlgoInstance = gene2geneAlgoInstance;
		this.inputConnection2value = inputConnection2value;
		this.fitnessOutput = fitnessOutput;
		
		this.autoFinishWhenChildrenFinished = false;
	}

	
	
	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		// TODO Auto-generated method stub
		super.notifyInputAvailable(to);
	}

	

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		super.computationStateChanged(progress);

		if (!progress.getComputationState().isFinished())
			return;
		
		IComputationResult result = progress.getAlgoExecution().getResult();
		if (result.getResults().containsKey(fitnessOutput)) {
			// this is the expected fitness :-)
			resultFitness = (Double)result.getResults().get(fitnessOutput);
			messages.debugTech("received fitness "+resultFitness, getClass());
			
			autoFinishWhenChildrenFinished = true;
			updateProgressFromChildren();
		}
		
	}



	@Override
	public void initInputs(Map<IAlgoInstance, IAlgoExecution> instance2exec) {

		// don't. 
		// super.initInputs(instance2exec);
		
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

		// prepare the mapping
		// each gene is mapped with ME
		for (IAlgoInstance sub: algoInstancesToRun) {
			for (IConnection cIn: sub.getAllIncomingConnections()) {
				
				if (cIn.getFrom().getAlgoInstance().getAlgo() instanceof IntegerGeneAlgo<?>) {
					
					// genes act as input of several children algos
					// they should refer to ME to retrieve these values !
					instance2execForSubtasks.put(cIn.getFrom().getAlgoInstance(), this);
				}
					
			}
		}
		
		
		messages.traceTech("create sub executables", getClass());
		for (IAlgoInstance sub: algoInstancesToRun) {
			IAlgoExecution subExec = sub.execute(exec);
			subExec.setParent(this);
			instance2execForSubtasks.put(sub, subExec);
		}
		
		messages.traceTech("init links for sub executables", getClass());
		for (IAlgoInstance sub: algoInstancesToRun) {
			IAlgoExecution subExec = instance2execForSubtasks.get(sub);
			subExec.initInputs(instance2execForSubtasks);
		}
		
		messages.traceTech("add subtasks", getClass());
		for (IAlgoInstance sub: algoInstancesToRun) {
			IAlgoExecution subExec = instance2execForSubtasks.get(sub);
			addTask(subExec);
		}
		
	
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
		for (Map.Entry<IConnection,Object> connection2value: inputConnection2value.entrySet()) {
			
			IConnectionExecution cEx = getExecutableConnectionFor(connection2value.getKey());
			Object valueFromOuside = connection2value.getValue();
			cEx.forceValue(valueFromOuside);
		}
		
		// set the initial values of each child (for gene values)
		for (IAlgoInstance sub: algoInstancesToRun) {
			for (IConnection cIn: sub.getAllIncomingConnections()) {
				
				if (cIn.getFrom().getAlgoInstance().getAlgo() instanceof IntegerGeneAlgo<?>) {

					IConnectionExecution cEx = getExecutableConnectionFor(cIn);
					
					Object valueForGene = getValueForGene(cIn.getFrom().getAlgoInstance());
					
					cEx.forceValue(valueForGene);
					
				}
					
			}
		}
		
		
	}
	
	public Double getResultFitness() {
		return resultFitness;
	}
	
	public final AnIndividual getIndividual() {
		return individual; 
	}
	
	public int getIndividualId() {
		return individualId;
	}
	
	@Override
	public String getName() {
		return "ind "+individualId+" / "+individual.genome.name;
	}
	
	
	


}
