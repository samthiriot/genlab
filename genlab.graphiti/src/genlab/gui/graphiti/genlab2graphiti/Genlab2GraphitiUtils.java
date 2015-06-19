package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.editors.GenlabDiagramEditor;
import genlab.gui.listeners.WorkflowGUIEventsDispatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.ui.action.CommandAction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.command.FeatureCommand;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class Genlab2GraphitiUtils {

	public static final String KEY_WORKFLOW_TO_GRAPHITI_FILE = "graphiti_file";
	public static final String EXTENSION_FILE_MAPPING = ".mapping";

	/**
	 * A command which will link a pictogram element to the business object.
	 * Will be ran in the right transaction.
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	private static class LinkCommand implements Command {

		private final GraphitiFeatureProvider dfp;
		private final PictogramElement pictogramElement;
		private final Object res;
		
		public LinkCommand(GraphitiFeatureProvider dfp, PictogramElement pictogramElement, Object res) {
			this.dfp = dfp;
			this.pictogramElement = pictogramElement;
			this.res = res;
		}

		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		public void execute() {
			GLLogger.debugTech("linking in "+dfp+" "+pictogramElement+" with "+res, getClass());
			dfp.link(pictogramElement, res);
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public void undo() {
		}

		@Override
		public void redo() {
		}

		@Override
		public Collection<?> getResult() {
			return null;
		}

		@Override
		public Collection<?> getAffectedObjects() {
			return null;
		}

		@Override
		public String getLabel() {
			return "internal linking of resources";
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public Command chain(Command command) {
			return null;
		}
		
	}
	
	private static class AddCommand implements Command {

		private final IAlgoInstance ai;
		private final GraphitiFeatureProvider gfp;
		
		public AddCommand(IAlgoInstance ai, GraphitiFeatureProvider gfp) {
			this.ai = ai;
			this.gfp = gfp;
		}

		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		public void execute() {
			GLLogger.infoTech("no pictogram for element "+ai+"; will add it to the diagram", Genlab2GraphitiUtils.class);
			
			AreaContext area = new AreaContext();
			AddContext ctxt = new AddContext(area, ai);
			IAddFeature addFeature = gfp.getAddFeature(ctxt);
			if (addFeature == null) {
				GLLogger.warnTech("no add feature for element "+ai+"; will NOT add it to the diagram: :-(", Genlab2GraphitiUtils.class);
			}
			PictogramElement peCreated = addFeature.add(ctxt);
			
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public void undo() {
		}

		@Override
		public void redo() {
		}

		@Override
		public Collection<?> getResult() {
			return null;
		}

		@Override
		public Collection<?> getAffectedObjects() {
			return null;
		}

		@Override
		public String getLabel() {
			return "internal add of pictogram";
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public Command chain(Command command) {
			return null;
		}
		
	}
	

	private static class FeatureCommand implements Command {

		private final IFeature f;
		private final IContext ctxt;

		public FeatureCommand(IFeature f, IContext ctxt) {
			this.f = f;
			this.ctxt = ctxt;
		}

		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		public void execute() {
			GLLogger.infoTech("executing feature "+f, Genlab2GraphitiUtils.class);
			
			f.execute(ctxt);
			
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public void undo() {
		}

		@Override
		public void redo() {
		}

		@Override
		public Collection<?> getResult() {
			return null;
		}

		@Override
		public Collection<?> getAffectedObjects() {
			return null;
		}

		@Override
		public String getLabel() {
			return "internal exec of featrure";
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public Command chain(Command command) {
			return null;
		}
		
	}
	/**
	 * Applies the grapĥiti link() in the relevant context (that is, a transaction and bla bla bla)
	 * @param dfp
	 * @param pictogramElement
	 * @param businessObject
	 */
	public static void linkInTransaction(GraphitiFeatureProvider dfp, PictogramElement pictogramElement, Object businessObject) {
		// retrieve resources
		//final ResourceSetImpl resourceSet = new ResourceSetImpl();
	    
		final ResourceSet resourceSet = pictogramElement.eResource().getResourceSet();
		
	
        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }

		editingDomain.getCommandStack().execute(
				new LinkCommand(
						dfp, 
						pictogramElement, 
						businessObject
						)
				);

        
	}
	
	protected static void addInTransaction(IAlgoInstance ai, GraphitiFeatureProvider gfp, Diagram diagram) {

		
		final Command cmd = new AddCommand(ai, gfp);
		
		final ResourceSet resourceSet = diagram.eResource().getResourceSet();

        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        
        editingDomain.getCommandStack().execute(
				cmd
				);
			
	}
	
	protected static void deleteInTransaction(Diagram diagram, final PictogramElement pe) {

		final ResourceSet resourceSet = diagram.eResource().getResourceSet();

        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }        
		final Command cmd = new Command() {
			
			@Override
			public void undo() {
			}
			
			@Override
			public void redo() {
				
			}
			
			@Override
			public Collection<?> getResult() {
				return Collections.EMPTY_LIST;
			}
			
			@Override
			public String getLabel() {
				return "delete picto";
			}
			
			@Override
			public String getDescription() {
				return "deletion of a picto";
			}
			
			@Override
			public Collection<?> getAffectedObjects() {
				Collection<Object> res = new LinkedList<Object>();
				res.add(pe);
				return res;
			}
			
			@Override
			public void execute() {
				Graphiti.getPeService().deletePictogramElement(pe);
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public Command chain(Command command) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean canUndo() {
				return false;
			}
			
			@Override
			public boolean canExecute() {
				return true;
			}
		}; 
				//new DeleteCommand(editingDomain, toDelete);
		
        editingDomain.getCommandStack().execute(
				cmd
				);
			
	}


protected static void ExecuteInTransaction(IUpdateFeature feature, IContext ctxt, Diagram diagram) {

		
		final ResourceSet resourceSet = diagram.eResource().getResourceSet();

        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        
        if (ctxt == null)
        	ctxt = new CustomContext();
        
        FeatureCommand fc = new FeatureCommand(feature, ctxt);

        editingDomain.getCommandStack().execute(fc);
			
	}

protected static void ExecuteInTransaction(ICustomFeature feature, IContext ctxt, Diagram diagram) {

		
		final ResourceSet resourceSet = diagram.eResource().getResourceSet();

        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        
        if (ctxt == null)
        	ctxt = new CustomContext();
        
        FeatureCommand fc = new FeatureCommand(feature, ctxt);
        
        editingDomain.getCommandStack().execute(fc);
			
	}
	
	public static void fillGraphitiFromGenlab(
			IGenlabWorkflowInstance workflow, 
			Diagram diagram, 
			GraphitiFeatureProvider gfp) {
		
		for (IAlgoInstance ai: workflow.getAlgoInstances()) {
		
			// search for its diagram counterpart
			PictogramElement pe = gfp.getPictogramElementForBusinessObject(ai);
			if (pe == null) {
				// TODO change info => debug
				GLLogger.infoTech("no pictogram for element "+ai+"; will add it to the diagram", Genlab2GraphitiUtils.class);
			
				try {
					addInTransaction(ai, gfp, diagram);
				} catch (RuntimeException e) {
					GLLogger.errorTech("error while adding a pictogram to element "+ai+"; the corresponding graphic representation will not be used.", Genlab2GraphitiUtils.class, e);
				}
			}
			
			
			
		}
		
		
		
	}
	
	// TODO remove old code for errors
	public static void createDiagram(IGenlabWorkflowInstance workflow, IProject project) {
		
		GLLogger.debugTech("creating a diagram for this workflow", Genlab2GraphitiUtils.class);
		
		// retrieve resources
		final ResourceSetImpl resourceSet = new ResourceSetImpl();
	        
        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        
        
        // Create the data within a command and save (must not happen inside
		// the command since finishing the command will trigger setting the 
		// modification flag on the resource which will be used by the save
		// operation to determine which resources need to be saved)
		AddAllClassesCommand operation = new AddAllClassesCommand(project, editingDomain, workflow);
		editingDomain.getCommandStack().execute(operation);
		try {
			operation.getCreatedResource().save(null);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, "org.eclipse.graphiti.examples.tutorial", e.getMessage(), e); //$NON-NLS-1$
			e.printStackTrace();
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "oops", e.getMessage(), status);
			
		}
		
		// associate diagram with the workflow
		// TODO associate something ?
		// MappingObjects.register(operation.getDiagram(), workflow);
		// also associate the diagram file with the workflow
		//MappingObjects.register(workflow.getAbsolutePath(), workflow);
		//MappingObjects.register(workflow.getAbsolutePath()+"."+GraphitiDiagramTypeProvider.GRAPH_EXTENSION, workflow);
		
		
		// Dispose the editing domain to eliminate memory leak
		editingDomain.dispose();
		
		// Open the editor
		String platformString = operation.getCreatedResource().getURI().toPlatformString(true);
		IFile file = project.getParent().getFile(new Path(platformString));
		IFileEditorInput input = new FileEditorInput(file);
		IEditorPart part = null;
		try {
			part  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					input, 
					GenlabDiagramEditor.EDITOR_ID
					);
			WorkflowGUIEventsDispatcher.workflowEvents.workflowShown(workflow);
		} catch (PartInitException e) {
			e.printStackTrace();
			IStatus status = new Status(IStatus.ERROR, "org.eclipse.graphiti.examples.tutorial", e.getMessage(), e); //$NON-NLS-1$
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "oops", e.getMessage(), status);
		}
		
		// TODO open only if asked by the user (not for users)
		// TODO layout of this diagram ? 
		
		// now populate everything !
		
		
	}

}
