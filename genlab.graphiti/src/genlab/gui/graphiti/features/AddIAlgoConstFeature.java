package genlab.gui.graphiti.features;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * From a constant, displays it
 * 
 * TODO manage color constants
 * 
 * @see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fselection-behavior.htm
 * 
 * @author Samuel Thiriot
 *
 */
public class AddIAlgoConstFeature extends AbstractAddFeature {

	public static final int WIDTH = 80;
	public static final int HEIGHT = 80;
	
	public static final int FONT_SIZE = 14;
	public static final String FONT_NAME = "Arial";
	
	public static final int MARGIN_WIDTH = 5;
	public static final int MARGIN_HEIGHT = 3;
	
	
	public AddIAlgoConstFeature(IFeatureProvider fp) {
		super(fp);
		
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		
		// we only manage novel algo instances
		if (!(context.getNewObject() instanceof IAlgoInstance))
			return false;
			
		IAlgoInstance ai = (IAlgoInstance)context.getNewObject();
		
		// and only constant algos
		if (! (ai.getAlgo() instanceof IConstantAlgo) )
			return false;
		
		// only allow the storage into the diagram (or into a container ?)
		Object boForContainer = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (boForContainer == null) {
			GLLogger.warnTech("unable to find the business object for a PE; problems ahead", getClass());
			return false;
		}
			
		final IAlgoInstance algoInstanceToAdd = (IAlgoInstance)context.getNewObject();

		// don't recreate the PE
		if (getFeatureProvider().getPictogramElementForBusinessObject(algoInstanceToAdd) != null)
			return false;
		
		IGenlabWorkflowInstance workflow = null;
		IAlgoContainerInstance container = null;
		
		if (boForContainer instanceof IGenlabWorkflowInstance) {
			workflow = (IGenlabWorkflowInstance)boForContainer;
			container = workflow;
		} else if (boForContainer instanceof IAlgoContainerInstance) {
			container = (IAlgoContainerInstance)boForContainer;
			workflow = container.getWorkflow();
		} else {
			// wrong container !
			return false;
		}
		
		if (!container.canContain(algoInstanceToAdd))
			return false;
		if (!algoInstanceToAdd.canBeContainedInto(container))
			return false;
			
		
		return true;
		
		
	
	}

	@Override
	public PictogramElement add(IAddContext context) {

		GLLogger.debugTech("creating a graphic representation for a constant", getClass());
		
		// retrieve parameters
		AlgoInstance addedAlgo = (AlgoInstance) context.getNewObject();
		IConstantAlgo algo = (IConstantAlgo) addedAlgo.getAlgo();

		// retrieve diagram
		ContainerShape contextContainerShape = context.getTargetContainer();
		GenlabWorkflowInstance workflow = null;
		Object boForContainer = this.getBusinessObjectForPictogramElement(context.getTargetContainer());
		
		if (boForContainer instanceof GenlabWorkflowInstance) {
			workflow = (GenlabWorkflowInstance)boForContainer;
			
		} else if (boForContainer instanceof IAlgoContainerInstance) {
			IAlgoContainerInstance algoContainer = (IAlgoContainerInstance)boForContainer;
			workflow = (GenlabWorkflowInstance) algoContainer.getWorkflow();
			addedAlgo.setContainer(algoContainer);
			
		} else 
			throw new WrongParametersException("wrong container, neither diagram/workflow nor container");
			
		
		addedAlgo._setWorkflowInstance(workflow);
		
		
		// create container 
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(contextContainerShape, true);

		
		// with default size..
		int width = Math.max(
				context.getWidth(), 
				WIDTH
				);
		int height = Math.max(
				context.getHeight(), 
				HEIGHT
				);
		
		IGaService gaService = Graphiti.getGaService();
		Rectangle invisibleRectangle ;

		// create and set graphics algo
		{
			// create invisible rectangle that allows for anchors that appear to be out of the fram
            invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
            gaService.setLocationAndSize(
            		invisibleRectangle,
            		context.getX(), 
            		context.getY(), 
            		width,
            		height
            		);

			// create link between the domain object and the graphical graphiti representation
			link(containerShape, addedAlgo);
			
		}
		
		
		// add text
		Text text;
		{
			Shape shape = peCreateService.createShape(containerShape, false);
			shape.setContainer(containerShape);
			
			final Object paramValue = addedAlgo.getValueForParameter(algo.getConstantParameter().getId()).toString();
			final String paramValueStr = paramValue.toString();
			
			text = gaService.createText(
					shape, 
					paramValueStr
					);
			text.setForeground(manageColor(IColorConstant.BLACK));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			final Font bigFont = gaService.manageFont(getDiagram(), FONT_NAME, FONT_SIZE, false, false);
			text.setFont(bigFont);
			
			IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(paramValueStr, bigFont);
			
			width = dim.getWidth()+MARGIN_WIDTH*2;
			height = dim.getHeight()+MARGIN_HEIGHT*2;
			
			gaService.setLocationAndSize(
					text, 
					MARGIN_WIDTH, 
					MARGIN_HEIGHT, 
					width-MARGIN_WIDTH, 
					height-MARGIN_HEIGHT
					);
			
			link(shape, addedAlgo);
		}
		
		 gaService.setLocationAndSize(
         		invisibleRectangle,
         		context.getX(), 
         		context.getY(), 
         		width,
         		height
         		);
		
		// add a connection point 
		{
//			Shape shape = peCreateService.createShape(containerShape, false);
			
/*
			BoxRelativeAnchor anchor = peCreateService.createBoxRelativeAnchor(shape);
			anchor.setRelativeHeight(0.5);
			anchor.setRelativeWidth(0.8);
			anchor.setReferencedGraphicsAlgorithm(invisibleRectangle);
			//anchor.setParent(containerShape);
			
	        Ellipse ellipse = gaService.createEllipse(anchor);
			ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
			ellipse.setBackground(manageColor(IColorConstant.WHITE));
			ellipse.setLineWidth(2);
			ellipse.setWidth(LayoutIAlgoFeature.ANCHOR_WIDTH);
			ellipse.setParentGraphicsAlgorithm(text);
			
			gaService.setLocationAndSize(
					ellipse, 
					0, 0, 
					LayoutIAlgoFeature.ANCHOR_WIDTH, LayoutIAlgoFeature.ANCHOR_WIDTH, 
					false
					);
	    */    
			ChopboxAnchor anchor = peCreateService.createChopboxAnchor(containerShape);
			anchor.setActive(true);
		

			link(anchor, addedAlgo.getOutputInstanceForOutput(algo.getConstantOuput()));
		}
		
        layoutPictogramElement(containerShape);

		
	      
		return containerShape;
	}
	
	

}
