package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.InputOutputInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

public class ComputationResult implements IComputationResult {

	protected final IAlgoInstance algo;
	protected final IComputationProgress progress;
	public Map<IInputOutputInstance,Object> results;

	protected final ListOfMessages listOfMessages = new ListOfMessages();
	
	public ComputationResult(IAlgoInstance algo, IComputationProgress progress) {
		this.algo = algo;
		this.progress = progress;
	}
	
	public ComputationResult(IAlgoInstance algo, IComputationProgress progress, Map<IInputOutputInstance,Object> results) {
		this.algo = algo;
		this.progress = progress;
		this.results = results;
	}

	@Override
	public IAlgoInstance getOriginalAlgo() {
		return algo;
	}

	@Override
	public IComputationProgress getProgress() {
		return progress;
	}


	@Override
	public final Map<IInputOutputInstance,Object> getResults() {
		return results;
	}
	
	public void setResults(Map<IInputOutputInstance,Object> results) {
		if (results == null)
			results = new HashMap<IInputOutputInstance, Object>(algo.getOutputInstances().size());
		this.results.putAll(results);
	}
	
	public void setResult(IInputOutputInstance io, Object value) {
		if (!algo.getOutputInstances().contains(io))
			throw new ProgramException("This algo is not supposed to provide this output: "+io);
		if (results == null)
			results = new HashMap<IInputOutputInstance, Object>(algo.getOutputInstances().size());
		// TODO can we erase previous values ? 
		this.results.put(io, value);
	}
	
	public void setResult(IInputOutput<?> io, Object value) {
		setResult(getOriginalAlgo().getOutputInstanceForOutput(io), value);
	}
	
	public void addMessage(ITextMessage message) {
		listOfMessages.add(message);
	}
	
	public void addMessages(Iterable<ITextMessage> messages) {
		listOfMessages.addAll(messages);
		
	}

	@Override
	public ListOfMessages getMessages() {
		return listOfMessages;
	}


	
}
