package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.algos.IGenlabWorkflow;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.util.Util;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class AddAllClassesCommand extends RecordingCommand {

	private IProject project;
	private TransactionalEditingDomain editingDomain;
	private Resource createdResource;
	private IGenlabWorkflow workflow;

	public AddAllClassesCommand(IProject project, TransactionalEditingDomain editingDomain, IGenlabWorkflow workflow) {
		super(editingDomain);
		this.project = project;
		this.editingDomain = editingDomain;
		this.workflow = workflow;
	}

	@Override
	protected void doExecute() {
		
		Diagram diagram = Graphiti.getPeCreateService().createDiagram(
				GraphitiDiagramTypeProvider.GRAPH_TYPE_ID, 
				workflow.getName(), 
				true
				);
		
		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(
				diagram,
				GraphitiDiagramTypeProvider.PROVIDER_ID
				); //$NON-NLS-1$
        
		IFolder diagramFolder = project.getFolder(workflow.getRelativePath()); //$NON-NLS-1$
		IFile diagramFile = diagramFolder.getFile(
				workflow.getFilename()+
				"."+ //$NON-NLS-1$
				GraphitiDiagramTypeProvider.GRAPH_EXTENSION
				); 
		URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
		createdResource = editingDomain.getResourceSet().createResource(uri);
		createdResource.getContents().add(diagram);

		IFeatureProvider featureProvider = dtp.getFeatureProvider();
	
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}
}