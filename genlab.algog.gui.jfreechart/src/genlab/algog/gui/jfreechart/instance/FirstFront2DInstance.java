package genlab.algog.gui.jfreechart.instance;

import genlab.algog.gui.jfreechart.algos.FirstFront2DAlgo;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.ListParameter;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FirstFront2DInstance extends AlgoInstance {

	private transient LinkedList<Parameter<?>> localParameters = null;
	private transient ListParameter paramColX;
	private transient ListParameter paramColY;
	
	private transient Map<String,Parameter<?>> parameterId2value = new HashMap<String,Parameter<?>>();
	
	public FirstFront2DInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		initParameters();
	}

	public FirstFront2DInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		initParameters();
	}
	
	/**
	 * Takes the generic parameters from the algo;
	 * but adapts them to our peculiar case.
	 */
	private void initParameters() {
		
		localParameters = new LinkedList<Parameter<?>>();
		
		paramColX = new ListParameter(
				FirstFront2DAlgo.PARAM_COLUMN_X.getId()+".local/"+getId(), 
				FirstFront2DAlgo.PARAM_COLUMN_X.getName(), 
				FirstFront2DAlgo.PARAM_COLUMN_X.getDesc()
				);
		localParameters.add(paramColX);
		parameterId2value.put(paramColX.getId(), paramColX);

		paramColY = new ListParameter(
				FirstFront2DAlgo.PARAM_COLUMN_Y.getId()+".local/"+getId(), 
				FirstFront2DAlgo.PARAM_COLUMN_Y.getName(), 
				FirstFront2DAlgo.PARAM_COLUMN_Y.getDesc()
				);
		localParameters.add(paramColY);
		parameterId2value.put(paramColY.getId(), paramColY);
	}
	
	@Override
	public Parameter<?> getParameter(String id) {
		Parameter<?> res = parameterId2value.get(id);
		if (res != null)
			return res;
		res = algo.getParameter(id);
		return res;
	}

	@Override
	public boolean hasParameter(String id) {
		return parameterId2value.containsKey(id) || algo.hasParameter(id); 
	}
	
	public ListParameter getParameterColumnX() {
		return paramColX;
	}
	

	public ListParameter getParameterColumnY() {
		return paramColY;
	}
	
	@Override
	public Collection<Parameter<?>> getParameters() {
		// we return our local copy, modified with the data for this instance
		return localParameters;
	}
	
	
}
