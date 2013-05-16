package genlab.gui.graphiti.diagram;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.palette.WorkflowToolBehaviorProvider;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class GraphitiDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String GRAPH_TYPE_ID = "genlab.graphiti.diagtypes.workflow";
	public static final String PROVIDER_ID = "genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider";
	public static final String GRAPH_EXTENSION = "wfdiag";

	private IToolBehaviorProvider[] toolBehaviorProviders;


	public GraphitiDiagramTypeProvider() {
		super();
		GLLogger.debugTech("Graphiti diagram provider instanciated for graphtype "+GRAPH_TYPE_ID, getClass());
		
		// add feature provider
		setFeatureProvider(new GraphitiFeatureProvider(this));
		
		// create our behavior providers to return them later
		toolBehaviorProviders = new IToolBehaviorProvider[] { new WorkflowToolBehaviorProvider(this) };
		
		
	}
	
	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return toolBehaviorProviders;
	}
	
	
	
}
