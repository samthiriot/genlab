package genlab.core.algos;

import genlab.core.projects.IGenlabProject;

import java.io.File;


public interface IGenlabWorkflow extends IAlgo {

	public String getName();
	
	public boolean isValid();
	
	public void addAlgoInstance(IAlgoInstance algoInstance);
	
	public void removeAlgoInstance(IAlgoInstance algoInstance);
	
	public void connect(IAlgoInstance algoFrom, IInputOutput<?> fromOutput, IAlgoInstance algoTo, IInputOutput<?> toInput);
	
	public File getFilePersisted();
	
	public String getRelativePath();
	
	public String getRelativeFilename();

	public String getFilename();

	public IGenlabProject getProject();
	
}
