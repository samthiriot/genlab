package genlab.gui.graphiti.diagram;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.genlab2graphiti.GenlabNotificationService;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;
import genlab.gui.graphiti.palette.WorkflowToolBehaviorProvider;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class GraphitiDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String GRAPH_TYPE_ID = "genlab.graphiti.diagtypes.workflow";
	public static final String PROVIDER_ID = "genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider";
	
	/**
	 * file extension for graphiti workflow diagrams.
	 * Has to be lowercase.
	 */
	public static final String GRAPH_EXTENSION = "wfdiag";

	private IToolBehaviorProvider[] toolBehaviorProviders;


	public GraphitiDiagramTypeProvider() {
		super();
		GLLogger.debugTech("Graphiti diagram provider instanciated for graphtype "+GRAPH_TYPE_ID, getClass());
		
		// add feature provider
		setFeatureProvider(GraphitiFeatureProvider.getOrCreateFor(this));
		
		// create our behavior providers to return them later
		toolBehaviorProviders = new IToolBehaviorProvider[] { new WorkflowToolBehaviorProvider(this) };
		
	
	}
	
	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return toolBehaviorProviders;
	}

	@Override
	public INotificationService getNotificationService() {
		return new GenlabNotificationService(this);
	}

	@Override
	public String getContextId() {
		String res = super.getContextId();
		return res;
	}
	
	/**
	 * Enable the retrieval of a feature provider from the genlab workflow.
	 * @param workflow
	 * @param diagram
	 */
	private void registerDiagram(IGenlabWorkflowInstance workflow, Diagram diagram) {
		
		if (workflow == null)
			throw new ProgramException("oops.");
		
		((GraphitiFeatureProvider)getFeatureProvider()).associateWorkflowWithThisProvider(workflow);
		
	}
	
	
	@Override
	public void init(Diagram diagram, IDiagramEditor diagramEditor) {
		
		super.init(diagram, diagramEditor);
		
		GLLogger.debugTech("at an init step of a diagram for a diagram editor", getClass());
		
		IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance) getFeatureProvider().getBusinessObjectForPictogramElement(diagram);

		if (workflow != null)
			registerDiagram(workflow, diagram);

		if (workflow==null) {
			GLLogger.warnTech("too bad, not ready yet", getClass());
			return;

		}
		GLLogger.debugTech("now listening for this workflow", getClass());
		workflow.addListener(WorkflowListener.lastInstance);
			
	}
	
	public void init(Diagram diagram, IDiagramBehavior diagramBehavior) {
		super.init(diagram, diagramBehavior);
		
		GLLogger.debugTech("at an init step of a diagram for a diagram editor", getClass());

		IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance) getFeatureProvider().getBusinessObjectForPictogramElement(diagram);

		if (workflow != null)
			registerDiagram(workflow, diagram);
		
	}

	@Override
	public boolean isAutoUpdateAtRuntime() {
		// TODO Auto-generated method stub
		return super.isAutoUpdateAtRuntime();
	}

	@Override
	public boolean isAutoUpdateAtRuntimeWhenEditorIsSaved() {
		// TODO Auto-generated method stub
		return super.isAutoUpdateAtRuntimeWhenEditorIsSaved();
	}

	@Override
	public boolean isAutoUpdateAtStartup() {
		// TODO Auto-generated method stub
		return super.isAutoUpdateAtStartup();
	}

	@Override
	public boolean isAutoUpdateAtReset() {
		// TODO Auto-generated method stub
		return super.isAutoUpdateAtReset();
	}
	

	
}
