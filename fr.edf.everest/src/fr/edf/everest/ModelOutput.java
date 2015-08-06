package fr.edf.everest;

import genlab.core.model.meta.IInputOutput;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ModelOutput {

	public final String entityTypeName;
	public final String PID;
	public final String KPI;
	
	protected final List<IInputOutput<?>> genlabOutputs = new LinkedList<>();

	public ModelOutput(String entityTypeName, String PID, String KPI) {
		this.entityTypeName = entityTypeName;
		this.PID = PID;
		this.KPI = KPI;
		
	}

	public List<IInputOutput<?>> getGenlabOutputs() {
		return Collections.unmodifiableList(genlabOutputs);
	}
	
	public abstract Map<IInputOutput<?>,Object> getOutputValuesForGenlab(List<Double> modelOutputs);
	
}
