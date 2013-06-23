package genlab.gui.graphiti.diagram;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.features.AddConnectionFeature;
import genlab.gui.graphiti.features.AddIAlgoInstanceConnectionFeature;
import genlab.gui.graphiti.features.CreateDomainObjectConnectionConnectionFeature;
import genlab.gui.graphiti.features.CreateIAlgoInstanceFeature;
import genlab.gui.graphiti.features.DeleteIAlgoInstanceFeature;
import genlab.gui.graphiti.features.LayoutIAlgoFeature;
import genlab.gui.graphiti.features.OpenParametersFeature;
import genlab.gui.graphiti.features.RemoveIAlgoInstanceFeature;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.genlab2graphiti.MappingObjects;
import genlab.gui.graphiti.patterns.DomainObjectPattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;

/**
 * TODO higlight everything that may be linked after first click
 * 
 * @author B12772
 *
 */
public class GraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	private GenLabIndependenceSolver independenceSolver;
	
	
	public GraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		GLLogger.debugTech("Graphiti feature provider instanciated: "+getClass().getCanonicalName(), getClass());
		addPattern(new DomainObjectPattern());
		
		independenceSolver = new GenLabIndependenceSolver(null);
		setIndependenceSolver(independenceSolver);
		
        
	}
	
	public void _setIndependanceSolver(GenLabIndependenceSolver solver) {
		this.independenceSolver = solver;
		setIndependenceSolver(independenceSolver);
	}
	
	public GenLabIndependenceSolver getIndependanceSolver() {
		return this.independenceSolver;
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {new CreateDomainObjectConnectionConnectionFeature(this)};
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {

		System.err.println(context);

		if (context.getNewObject() == null) {
			// special case of an empty link
			// TODO
			return null;
		}
		System.err.println(context.getNewObject());

		
		// provide the add connection depending to the type
		
		// to add algo instance
		if (context.getNewObject() instanceof IAlgoInstance) {
			return new AddIAlgoInstanceConnectionFeature(this);
		}

		// to add connection
		if (context.getNewObject() instanceof IConnection) {
			IConnection c = (IConnection)context.getNewObject();
			return new AddConnectionFeature(this);
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+context.getNewObject(), getClass());
		
		return super.getAddFeature(context);
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures() {
		
		List<IAlgo> algos = new ArrayList(ExistingAlgos.getExistingAlgos().getAlgos());
		
		ICreateFeature[] res = new ICreateFeature[algos.size()];
		
		// add all the possible algos
		for (int i=0; i<algos.size(); i++) {
			IAlgo algo = algos.get(i);
			res[i] = new CreateIAlgoInstanceFeature(this, algo);
		}
		
		return res;
	}
	
	
	
	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		
		ICustomFeature[] ret = super.getCustomFeatures(context);
		List<ICustomFeature> retList = new ArrayList<ICustomFeature>();
		// retrieve parent features
		for (int i = 0; i < ret.length; i++) {
			retList.add(ret[i]);
		}
		
		// add mine
		retList.add(new OpenParametersFeature(this));
		
		return retList.toArray(new ICustomFeature[retList.size()]);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {

		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		if (bo  instanceof IAlgoInstance) {
			return new RemoveIAlgoInstanceFeature(this); // no remove for our object; everything displayed is real, everything real is displayed.
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+bo, getClass());
		
		return super.getRemoveFeature(context);
	}
	
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {

		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());

		if (bo instanceof IAlgoInstance) {
			return new DeleteIAlgoInstanceFeature(this); 
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+bo, getClass());
		
		return super.getDeleteFeature(context);
	}



	public GenLabIndependenceSolver getGenlabIndependenceSolver() {
		return independenceSolver;
	}

	
	/**
	 * provides drag and drop for anchors
	 */
	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		
		GLLogger.traceTech("received event: drag drop: "+context.getPictogramElement(), getClass());
		if (context.getPictogramElement() != null) {
			Object o = getBusinessObjectForPictogramElement(context.getPictogramElement());
			GLLogger.traceTech("received event: drag drop obj: "+o, getClass());
			
		}
		
	    // simply return all create connection features
	    return getCreateConnectionFeatures();
	} 

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		if (bo instanceof IAlgoInstance) {
			return new LayoutIAlgoFeature(this); 
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+bo, getClass());
		
		
		return super.getLayoutFeature(context);
	}
	
	private class LinkCommand implements Command {

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
			link(pictogramElement, res);
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
	
	
	@Override
	public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
		
		Object res = super.getBusinessObjectForPictogramElement(pictogramElement);
		
		if (res == null) {
			
			// start of problem solving...
			
			// maybe this is a diagram, and diagrams have problems of mapping corrected by us
			if (pictogramElement instanceof Diagram) {
				res = MappingObjects.getGenlabResourceFor(pictogramElement);
			}
			
			// store mapping
			if (res != null)
				TransactionUtil.getEditingDomain(pictogramElement).getCommandStack().execute(
						new LinkCommand(this, pictogramElement, res)
						);
			
		}
		
		return res;
	}

	@Override
	public void link(PictogramElement pictogramElement, Object businessObject) {
		// TODO Auto-generated method stub
		super.link(pictogramElement, businessObject);
	}

	@Override
	public void link(PictogramElement pictogramElement, Object[] businessObjects) {
		// TODO Auto-generated method stub
		super.link(pictogramElement, businessObjects);
	}

	
}
