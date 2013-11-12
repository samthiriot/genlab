package genlab.gui.graphiti.diagram;

import genlab.core.model.instance.Connection;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.features.AddConnectionFeature;
import genlab.gui.graphiti.features.AddIAlgoConstFeature;
import genlab.gui.graphiti.features.AddIAlgoContainerFeature;
import genlab.gui.graphiti.features.AddIAlgoInstanceFeature;
import genlab.gui.graphiti.features.AlgoDirectEditingFeature;
import genlab.gui.graphiti.features.AlgoUpdateFeature;
import genlab.gui.graphiti.features.ConstDirectEditingFeature;
import genlab.gui.graphiti.features.ConstUpdateFeature;
import genlab.gui.graphiti.features.CopyFeature;
import genlab.gui.graphiti.features.CreateDomainObjectConnectionConnectionFeature;
import genlab.gui.graphiti.features.CreateIAlgoInstanceFeature;
import genlab.gui.graphiti.features.DeleteConnectionFeature;
import genlab.gui.graphiti.features.DeleteIAlgoInstanceFeature;
import genlab.gui.graphiti.features.LayoutConstFeature;
import genlab.gui.graphiti.features.LayoutIAlgoFeature;
import genlab.gui.graphiti.features.OpenParametersFeature;
import genlab.gui.graphiti.features.PasteFeature;
import genlab.gui.graphiti.features.RemoveIAlgoInstanceFeature;
import genlab.gui.graphiti.features.SeeInfoFeature;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.patterns.DomainObjectPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.ExternalPictogramLink;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.services.Graphiti;

/**
 * TODO higlight everything that may be linked after first click
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	/**
	 * Key used to associate a genlab workflow with its graphiti feature provider
	 */
	protected final static String KEY_FEATURE_PROVIDER_FOR_WORKFLOW = "genlab.graphiti.featureprovider";

	private GenLabIndependenceSolver independenceSolver;
	
	private static Map<IDiagramTypeProvider,GraphitiFeatureProvider> type2provider = new HashMap<IDiagramTypeProvider, GraphitiFeatureProvider>();
	
	protected CreateDomainObjectConnectionConnectionFeature createConnection = new CreateDomainObjectConnectionConnectionFeature(this);
	
	protected AddConnectionFeature addConnection = new AddConnectionFeature(this);
	protected AddIAlgoConstFeature addConstant = new AddIAlgoConstFeature(this);
	protected AddIAlgoContainerFeature addContainer = new AddIAlgoContainerFeature(this);
	protected AddIAlgoInstanceFeature addAlgo = new AddIAlgoInstanceFeature(this);
	
	protected RemoveIAlgoInstanceFeature removeAlgo = new RemoveIAlgoInstanceFeature(this);
	protected DeleteIAlgoInstanceFeature deleteAlgo = new DeleteIAlgoInstanceFeature(this);
	protected DeleteConnectionFeature deleteConnection = new DeleteConnectionFeature(this);

	protected LayoutIAlgoFeature layoutAlgo = new LayoutIAlgoFeature(this);
	protected LayoutConstFeature layoutConst = new LayoutConstFeature(this);
	
	protected ConstDirectEditingFeature directEditConst = new ConstDirectEditingFeature(this);
	protected AlgoDirectEditingFeature directEditAlgo = new AlgoDirectEditingFeature(this);

	protected ConstUpdateFeature updateConst = new ConstUpdateFeature(this);
	protected AlgoUpdateFeature updateAlgo = new AlgoUpdateFeature(this);
	
	protected OpenParametersFeature customOpenParam = new OpenParametersFeature(this);
	protected SeeInfoFeature customViewInfo = new SeeInfoFeature(this);

	public static GraphitiFeatureProvider getOrCreateFor(IDiagramTypeProvider dtp) {
		GraphitiFeatureProvider res = type2provider.get(dtp);
		if (res == null) {
			res = new GraphitiFeatureProvider(dtp);
			type2provider.put(dtp, res);
		}
		return res;
	}
	
	protected GraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		
		type2provider.put(dtp, this);

        
		GLLogger.debugTech("Graphiti feature provider instanciated: "+getClass().getCanonicalName(), getClass());
		addPattern(new DomainObjectPattern());
		
		independenceSolver = GenLabIndependenceSolver.singleton;
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
		return new ICreateConnectionFeature[] { createConnection };
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {

		if (context.getNewObject() == null) {
			// special case of an empty link
			// TODO
			return null;
		}
		
		// provide the add connection depending to the type
		
		// to add algo instance
		if (context.getNewObject() instanceof IAlgoInstance) {
			IAlgoInstance ai = (IAlgoInstance)context.getNewObject();
			if (ai.getAlgo() instanceof IConstantAlgo)
				return addConstant;
			else if (ai instanceof IAlgoContainerInstance)
				return addContainer;
			else 
				return addAlgo;
		}

		// to add connection
		if (context.getNewObject() instanceof IConnection) {
			IConnection c = (IConnection)context.getNewObject();
			return addConnection;
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+context.getNewObject(), getClass());
		
		return super.getAddFeature(context);
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures() {
		
		GLLogger.debugTech("creating the list of create features...", getClass());
		
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
		retList.add(customOpenParam);
		retList.add(customViewInfo);
		
		return retList.toArray(new ICustomFeature[retList.size()]);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {

		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		if (bo  instanceof IAlgoInstance) {
			return removeAlgo; // no remove for our object; everything displayed is real, everything real is displayed.
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+bo, getClass());
		
		return super.getRemoveFeature(context);
	}
	
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {

		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());

		if (bo instanceof IAlgoInstance) {
			return deleteAlgo; 
		} else if (bo instanceof Connection) {
			return deleteConnection;
		}
		
		GLLogger.warnTech("cannot provide a feature to delete this object: "+bo, getClass());
		
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
		
		/*
		GLLogger.traceTech("received event: drag drop: "+context.getPictogramElement(), getClass());
		if (context.getPictogramElement() != null) {
			Object o = getBusinessObjectForPictogramElement(context.getPictogramElement());
			GLLogger.traceTech("received event: drag drop obj: "+o, getClass());
			
		}
		*/
		
	    // simply return all create connection features
	    return getCreateConnectionFeatures();
	} 

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		if (bo instanceof IAlgoInstance) {
			IAlgoInstance ai = (IAlgoInstance)bo;
			if (ai.getAlgo() instanceof IConstantAlgo) {
				return layoutConst;
			} else {
				return layoutAlgo;
			}
		}
		
		GLLogger.warnTech("cannot provide a feature for a wrong object: "+bo, getClass());
		
		
		return super.getLayoutFeature(context);
	}
	

	private static class StringTransformer {
		private final static String marker = "__independentN"; //$NON-NLS-1$

		String[] decode(String value) {
			if (!value.startsWith(marker)) {
				return new String[] { value };
			} else {
				value = value.substring(marker.length(), value.length());
				return value.split(marker);
			}
		}

		String encode(String[] segments) {
			if (segments.length == 1) {
				return segments[0];
			}
			StringBuffer sb = new StringBuffer();
			for (String string : segments) {
				sb.append(marker);
				sb.append(string);
			}
			return sb.toString();
		}
	}
	static StringTransformer st = new StringTransformer();

	public static String[] getValues(String value) {
		if (value.length() == 0) {
			return new String[0];
		} else {
			return st.decode(value);
		}
	}
	
	
	@Override
	public PictogramElement getPictogramElementForBusinessObject(Object businessObject) {
		
		// for a given business object, we are asked the corresponding pictogram
		// the parent, default class does its work well for standard cases, that is when we are looking for the element
		// in this case, the default behaviour is to explore the diagram to find the relevant picto
		
		// but for picto elements, we first have to find the relevant 
		
		/*
		if (businessObject instanceof IGenlabWorkflowInstance) {
			// specific case which is really not well managed by parent graphiti classes
			
			IGenlabWorkflowInstance workflowInstance = (IGenlabWorkflowInstance)businessObject;
			Object readen = workflowInstance.getTransientObjectForKey(KEY_PICTOELEMENT_WORKFLOW);
			
			if (readen == null)
				 throw new ProgramException("this workflow was not yet associated with a pictogram element");
			
			return (PictogramElement)readen;
						
		} 
		*/
		
		// if this is a diagram, the parent will not be able to find it (yep, this is dumb)
		String keyForBusinessObject = getIndependanceSolver().getKeyForBusinessObject(businessObject);
		Property property = Graphiti.getPeService().getProperty(getDiagramTypeProvider().getDiagram(), ExternalPictogramLink.KEY_INDEPENDENT_PROPERTY);
		if (property != null && Arrays.asList(getValues(property.getValue())).contains(keyForBusinessObject)) {
			return getDiagramTypeProvider().getDiagram();
		}		
		
		return super.getPictogramElementForBusinessObject(businessObject);
		
	}

	@Override
    public IDirectEditingFeature getDirectEditingFeature(
    			IDirectEditingContext context
    			) {
		
		
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof IAlgoInstance) {
			IAlgoInstance ai = (IAlgoInstance)bo;
			if (ai.getAlgo() instanceof IConstantAlgo) {
				return directEditConst;
			} else {
				return directEditAlgo; 
			}
		}
		
		return super.getDirectEditingFeature(context);
	}
	

    @Override
    public IUpdateFeature getUpdateFeature(IUpdateContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        if (pictogramElement instanceof ContainerShape) {
            Object bo = getBusinessObjectForPictogramElement(pictogramElement);
            if (bo instanceof IAlgoInstance) {
            	IAlgoInstance ai = (IAlgoInstance)bo;
    			if (ai.getAlgo() instanceof IConstantAlgo) {
    				return updateConst;
    			} else {
                    return updateAlgo;
    			}
            }
        }
        return super.getUpdateFeature(context);
    }
 
    @Override
    public ICopyFeature getCopyFeature(ICopyContext context) {
        return new CopyFeature(this);
    }
     
    
    @Override
    public IPasteFeature getPasteFeature(IPasteContext context) {
        return new PasteFeature(this);
    }
    
	public void associateWorkflowWithThisProvider(IGenlabWorkflowInstance workflow) {
		
		workflow.addTransientObjectForKey(
				GraphitiFeatureProvider.KEY_FEATURE_PROVIDER_FOR_WORKFLOW, 
				this
				);

		
		GLLogger.traceTech("registered feature provider "+this+" for workflow "+workflow, getClass());

	}
	
	public static GraphitiFeatureProvider getFeatureProviderForWorkflow(IGenlabWorkflowInstance workflow) {
		return (GraphitiFeatureProvider) workflow.getTransientObjectForKey(KEY_FEATURE_PROVIDER_FOR_WORKFLOW);
	}

}
