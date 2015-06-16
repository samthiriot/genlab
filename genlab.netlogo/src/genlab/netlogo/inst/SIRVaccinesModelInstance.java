package genlab.netlogo.inst;

import java.util.Collections;
import java.util.LinkedList;

import genlab.core.model.instance.AlgoInstanceWithParametersDependantToConnections;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.TextMessageFromAlgoInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.ListParameter;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.netlogo.algos.SIRModelAlgo;
import genlab.netlogo.algos.SIRVaccinesModelAlgo;

@SuppressWarnings("serial")
public class SIRVaccinesModelInstance extends
		AlgoInstanceWithParametersDependantToConnections {

	public ListParameter PARAM_COLUMN_BETWEENESS;
	public ListParameter PARAM_COLUMN_DEGREE;
	
	public SIRVaccinesModelInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public SIRVaccinesModelInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	protected void declareLocalParameters() {
		if (PARAM_COLUMN_BETWEENESS == null)
			PARAM_COLUMN_BETWEENESS = new ListParameter(
					"param_column_betweeness", 
					"attribute for betweeness", 
					"attribute to read for the betweeness"
					);
		if (PARAM_COLUMN_DEGREE == null)
			PARAM_COLUMN_DEGREE = new ListParameter(
					"param_column_degree", 
					"attribute for degree", 
					"attribute to read for the degree"
					);
		declareParameter(PARAM_COLUMN_DEGREE);
		declareParameter(PARAM_COLUMN_BETWEENESS);

	}

	@Override
	protected void adaptParametersToInputs() {

		try {
			IGenlabGraph g = (IGenlabGraph)getPrecomputedValueForInput(SIRVaccinesModelAlgo.INPUT_GRAPH);
			
			PARAM_COLUMN_BETWEENESS.setItems(new LinkedList<String>(g.getDeclaredVertexAttributes()));
			PARAM_COLUMN_DEGREE.setItems(new LinkedList<String>(g.getDeclaredVertexAttributes()));
			
		} catch (RuntimeException e) {
			PARAM_COLUMN_BETWEENESS.setItems(Collections.EMPTY_LIST);
			PARAM_COLUMN_DEGREE.setItems(Collections.EMPTY_LIST);
		}
	}

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		super.checkForRun(res);
		
		// check the attributes of the graph
		Object precomputedValue = getPrecomputedValueForInput(SIRModelAlgo.INPUT_GRAPH);
		
		IGenlabGraph inGraph = (IGenlabGraph)precomputedValue;
		
		// load the parameters as well
		if (PARAM_COLUMN_BETWEENESS.getItems().isEmpty()) {
			res.messages.add(new TextMessageFromAlgoInstance(
					this, 
					MessageLevel.ERROR, 
					"the input graph has no attribute, so there is no possibility to use the vertex betweeness nor the vertex degree"
					)
			);
			return;
		}
		
		// check the graph has the vertex attribute for betweeness
		{
			final Integer paramAttributeForVertexIdx = (Integer)getValueForParameter(PARAM_COLUMN_BETWEENESS);
			final String paramAttributeValue = PARAM_COLUMN_BETWEENESS.getLabel(paramAttributeForVertexIdx);
			if (!inGraph.hasVertexAttribute(paramAttributeValue)) {
				res.messages.add(new TextMessageFromAlgoInstance(
						this, 
						MessageLevel.ERROR, 
						"the input graph has no attribute containing the vertex betweeness"
						)
				);
			}
		}
		
		// check the graph has the vertex attribute for degree
		{
			final Integer paramAttributeForVertexIdx = (Integer)getValueForParameter(PARAM_COLUMN_DEGREE);
			final String paramAttributeValue = PARAM_COLUMN_DEGREE.getLabel(paramAttributeForVertexIdx);
			if (!inGraph.hasVertexAttribute(paramAttributeValue)) {
				res.messages.add(new TextMessageFromAlgoInstance(
						this, 
						MessageLevel.ERROR, 
						"the input graph has no attribute containing the vertex degree"
						)
				);
			}
		}
		
		
		
	}

}
