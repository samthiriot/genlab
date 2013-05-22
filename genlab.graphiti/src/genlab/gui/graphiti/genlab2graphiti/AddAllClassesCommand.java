package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.algos.IGenlabWorkflow;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class AddAllClassesCommand extends RecordingCommand {

	private IProject project;
	private TransactionalEditingDomain editingDomain;
	private Resource createdResource;
	private IGenlabWorkflow workflow;
	private Diagram diagram = null;

	public AddAllClassesCommand(IProject project, TransactionalEditingDomain editingDomain, IGenlabWorkflow workflow) {
		super(editingDomain);
		this.project = project;
		this.editingDomain = editingDomain;
		this.workflow = workflow;
	}

	@Override
	protected void doExecute() {
		
		diagram = Graphiti.getPeCreateService().createDiagram(
				GraphitiDiagramTypeProvider.GRAPH_TYPE_ID, 
				workflow.getName(), 
				true
				);
		
		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(
				diagram,
				GraphitiDiagramTypeProvider.PROVIDER_ID
				); 
		
		//((GraphitiFeatureProvider)dtp.getFeatureProvider()).getIndependanceSolver().
		
        
		IFolder diagramFolder = project.getFolder(project.getProjectRelativePath().append(workflow.getRelativePath())); //$NON-NLS-1$
		IFile diagramFile = diagramFolder.getFile(
				workflow.getFilename()+
				"."+ //$NON-NLS-1$
				GraphitiDiagramTypeProvider.GRAPH_EXTENSION
				); 
		URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
		createdResource = editingDomain.getResourceSet().createResource(uri);
		createdResource.getContents().add(diagram);
	
		MappingObjects.register(diagram, workflow);
		
		// add a link between the workflow and the diagram file
		workflow.addObjectForKey(
				Genlab2GraphitiUtils.KEY_WORKFLOW_TO_GRAPHITI_FILE, 
				workflow.getRelativeFilename()
				);

	}
	
	public Diagram getDiagram() {
		return diagram;
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}
}