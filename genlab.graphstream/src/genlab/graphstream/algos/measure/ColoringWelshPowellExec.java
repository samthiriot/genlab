package genlab.graphstream.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.AbstractGraphstreamBasedGraph;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.coloring.WelshPowell;
import org.graphstream.graph.Graph;

public class ColoringWelshPowellExec extends AbstractGraphstreamMeasureExecution implements IAlgoExecutionRemotable {
	
	public ColoringWelshPowellExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}
	

	public ColoringWelshPowellExec() {}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 1000;
	}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, Graph gsGraph,
			IGenlabGraph genlabGraph, ListOfMessages messages) {
		
		Map<IInputOutput<?>, Object> res = new HashMap<IInputOutput<?>, Object>(5);
		
		IGenlabGraph outGraph = genlabGraph.clone("dup");
		String attributeId = (String)algoInst.getValueForParameter(ColoringWelshPowell.PARAM_ATTRIBUTE_NAME);
		
		if (isUsed(ColoringWelshPowell.OUTPUT_GRAPH) || exec.getExecutionForced()) {
			
			outGraph.declareVertexAttribute(attributeId, Integer.class);
			
		}
		
		Graph outGSgraph = ((AbstractGraphstreamBasedGraph)outGraph)._getInternalGraphstreamGraph();
		
		WelshPowell wp = new WelshPowell(attributeId);
		wp.init(outGSgraph);
		wp.compute();

		res.put(ColoringWelshPowell.OUTPUT_COLORS_COUNT, wp.getChromaticNumber());
		res.put(ColoringWelshPowell.OUTPUT_GRAPH, outGraph);
		
		return res;
	}
}