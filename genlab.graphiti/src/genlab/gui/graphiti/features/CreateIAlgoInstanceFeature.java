package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.GraphitiImageProvider;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * For an algo taken from the palette, creates an instance in the diagram.
 * 
 * @author Samuel Thiriot
 *
 */
public class CreateIAlgoInstanceFeature extends AbstractCreateFeature {

	private final IAlgo algo;
	
	public CreateIAlgoInstanceFeature(IFeatureProvider fp, IAlgo algo) {
		super(fp, algo.getName(), algo.getDescription());
		this.algo = algo;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		
		// always create into something
		if (context.getTargetContainer() == null)
			return false;
		
		Object boTarget = getBusinessObjectForPictogramElement(context.getTargetContainer());
		
		// only accept creation into containers
		if (!(boTarget instanceof IAlgoContainerInstance))
			return false;
		
		IAlgoContainerInstance boTargetContainer = (IAlgoContainerInstance)boTarget;
		IAlgoContainer boTargetContainerMeta = (IAlgoContainer)boTargetContainer.getAlgo();
				
		return (boTargetContainerMeta.canContain(algo) && algo.canBeContainedInto(boTargetContainer));
		
	}

	@Override
	public Object[] create(final ICreateContext context) {
		/*
		// ask for the algo name
		String newClassName = UIUtils.askString(
				"name of this algo ?", 
				"truc", 
				"");
		// ... valid ? 
		if (newClassName == null || newClassName.trim().length() == 0) {
			return EMPTY;
		}
		*/
		
		// create instance
		GLLogger.debugTech("adding an algo instance for algo "+algo.getName(), getClass());

		// TODO should add this to the workflow !
		
		Object bo = getBusinessObjectForPictogramElement(
				context.getTargetContainer()
				);
		if (bo == null) {
			GLLogger.warnTech("unable to find the business object related to this diagram, problems ahead", getClass());
			return null;
		}
		
		IGenlabWorkflowInstance workflow = null;
		IAlgoContainerInstance container = null;
		if (bo instanceof IGenlabWorkflowInstance) {
			workflow = (IGenlabWorkflowInstance)bo;
		} else if (bo instanceof IAlgoContainerInstance) {
			container = (IAlgoContainerInstance)bo;
			workflow = container.getWorkflow();
		}
		
		// create the instance 
		IAlgoInstance algoInstance = algo.createInstance(workflow);
				
		// transmit the position info to the workflow listener, which will then actually create the instance
		WorkflowListener.lastInstance.transmitLastUIParameters(
				algoInstance,
				new WorkflowListener.UIInfos() {{ 
					x = context.getX(); 
					y = context.getY();
					width = context.getWidth();
					height = context.getHeight();
					containerShape = context.getTargetContainer();
				}}
				);
		
		// add to the workflow
		if (container != null) {
			algoInstance.setContainer(container);
		}
		workflow.addAlgoInstance(algoInstance);
		if (container != null) {
			container.addChildren(algoInstance);
		}
		// the graphical representation will be created by reaction to workflow listener
		//addGraphicalRepresentation(context, algoInstance);

		return new Object[]{algoInstance};
		
		
	}
	
	public IAlgo getAlgo() {
		return algo;
	}

	@Override
	public String getCreateImageId() {
		if (algo.getImagePath16X16() == null)
			return null;
		return GraphitiImageProvider.getImageIdForAlgo(algo);
	}

	
	
}
