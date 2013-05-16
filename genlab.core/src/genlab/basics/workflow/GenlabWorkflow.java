package genlab.basics.workflow;

import genlab.core.algos.AlgoInstance;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.WrongParametersException;
import genlab.core.commons.FileUtils;
import genlab.core.projects.IGenlabProject;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenlabWorkflow implements IGenlabWorkflow {

	protected Set<IAlgoInstance> algoInstances = new HashSet<IAlgoInstance>();
	
	public IGenlabProject project;
	public String name;
	public String description;
	private String relativeFilename;
	
	public GenlabWorkflow(IGenlabProject project, String name, String description, String relativeFilename) {
		this.project = project;
		this.name = name;
		this.description = description;
		this.relativeFilename = relativeFilename;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAlgoInstance(IAlgoInstance algoInstance) {
		algoInstances.add(algoInstance);
	}

	@Override
	public void removeAlgoInstance(IAlgoInstance algoInstance) {
		algoInstances.remove(algoInstance);
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<IInputOutput> getInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoInstance createInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoExecution createExec(AlgoInstance algoInstance, Map<IInputOutput, Object> inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect(
			IAlgoInstance algoFrom, IInputOutput<?> fromOutput,
			IAlgoInstance algoTo, IInputOutput<?> toInput
			) {
		
		// ensure integrity
		
		if (!algoInstances.contains(algoFrom) || !algoInstances.contains(algoTo))
			throw new WrongParametersException("this instance of the algorithm does not belongs the workflow");
		
		if (!algoFrom.getAlgo().getOuputs().contains(fromOutput))
			throw new WrongParametersException("this output does not belong this algo");
		
		if (!algoTo.getAlgo().getInputs().contains(toInput))
			throw new WrongParametersException("this input does not belong this algo");
		
		Connection c = new Connection(algoFrom, fromOutput, algoTo, toInput);
		
	}

	@Override
	public File getFilePersisted() {
		return null;
	}

	@Override
	public IGenlabProject getProject() {
		return project;
	}

	@Override
	public String getRelativePath() {
		return FileUtils.extractPath(relativeFilename);
	}

	@Override
	public String getRelativeFilename() {
		return relativeFilename;
	}

	@Override
	public String getFilename() {
		return FileUtils.extractFilename(relativeFilename);
	}

}
