package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.StringParameter;

public class ColoringWelshPowell extends AbstractGraphStreamMeasure {

	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"colored graph"
	);
	
	public static final IntegerInOut OUTPUT_COLORS_COUNT = new IntegerInOut(
			"out_count", 
			"count", 
			"count of colors"
			);
	
	public static final StringParameter PARAM_ATTRIBUTE_NAME = new StringParameter(
			"param_attribute", 
			"attribute name", 
			"vertex attribute to use for color", 
			"color"
			);
	
	public ColoringWelshPowell() {
		super("coloring Welsh-Powell (graphstream)", "Welsh-Powell algorithm for the problem of graph coloring");
		
		outputs.add(OUTPUT_GRAPH);
		outputs.add(OUTPUT_COLORS_COUNT);
		
		registerParameter(PARAM_ATTRIBUTE_NAME);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,AlgoInstance algoInstance) {
		
		return new ColoringWelshPowellExec(execution, algoInstance);
	}

}
