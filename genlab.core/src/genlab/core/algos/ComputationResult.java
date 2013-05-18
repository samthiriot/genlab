package genlab.core.algos;

import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

public class ComputationResult implements IComputationResult {

	protected final IAlgo algo;
	protected final IComputationProgress progress;
	public Map<IInputOutput<?>,Object> results;

	protected final ListOfMessages listOfMessages = new ListOfMessages();
	
	public ComputationResult(IAlgo algo, IComputationProgress progress) {
		this.algo = algo;
		this.progress = progress;
	}
	
	public ComputationResult(IAlgo algo, IComputationProgress progress, Map<IInputOutput<?>,Object> results) {
		this.algo = algo;
		this.progress = progress;
		this.results = results;
	}

	@Override
	public IAlgo getOriginalAlgo() {
		return algo;
	}

	@Override
	public IComputationProgress getProgress() {
		return progress;
	}


	@Override
	public final Map<IInputOutput<?>,Object> getResults() {
		return results;
	}
	
	public void setResults(Map<IInputOutput<?>,Object> results) {
		if (results == null)
			results = new HashMap<IInputOutput<?>, Object>(algo.getOuputs().size());
		this.results.putAll(results);
	}
	
	public void setResult(InputOutput<?> io, Object value) {
		if (!algo.getOuputs().contains(io))
			throw new ProgramException("This algo is not supposed to provide this output: "+io);
		if (results == null)
			results = new HashMap<IInputOutput<?>, Object>(algo.getOuputs().size());
		// TODO can we erase previous values ? 
		this.results.put(io, value);
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
