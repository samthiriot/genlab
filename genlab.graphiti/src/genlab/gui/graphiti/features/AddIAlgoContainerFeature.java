package genlab.gui.graphiti.features;

import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoContainer;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.persistence.AlgoInstanceConverter;
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
 * TODO TODO
 * 
 * @see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fselection-behavior.htm
 * 
 * @author Samuel Thiriot
 *
 */
public class AddIAlgoContainerFeature extends AbstractAddFeature {

	public static final int WIDTH = 80;
	public static final int HEIGHT = 80;
	
	public static final int ROUNDED = 10;
	
	public static final int FONT_SIZE = 20;
	public static final String FONT_NAME = "Arial";
	
	public static final int MARGIN_WIDTH = 5;
	public static final int MARGIN_HEIGHT = 3;
	
	
	public AddIAlgoContainerFeature(IFeatureProvider fp) {
		super(fp);
		
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		
		// check what is added
		if (!(context.getNewObject() instanceof IAlgoContainerInstance))
			return false;
		
		
		IAlgoContainerInstance aiAdded = (IAlgoContainerInstance)context.getNewObject();
		
		if (! (aiAdded.getAlgo() instanceof AlgoContainer) )
			return false;
		
			
		// and to what it is added
		if (!(context.getTargetContainer() instanceof Diagram))
			return false;
				

		IGenlabWorkflowInstance workflowTarget = (IGenlabWorkflowInstance) getBusinessObjectForPictogramElement(
				context.getTargetContainer()
				);
		if (workflowTarget == null) {
			GLLogger.warnTech("unable to find the workflow for this diagram, problems ahead", getClass());
			return false;
		}
			

		if (!workflowTarget.canContain(aiAdded))
			return false;
		if (!aiAdded.canBeContainedInto(workflowTarget))
			return false;
			
		
		// don't add the same instance twice, that is...
		return	(
					// the algo instance is not already in the workflow 
					(!workflowTarget.containsAlgoInstance(aiAdded))
					||
					// or it still has no graphical representation
					(getFeatureProvider().getPictogramElementForBusinessObject(aiAdded) == null)
				);
		
	}

	@Override
	public PictogramElement add(IAddContext context) {

		GLLogger.debugTech("creating a graphic representation for a container", getClass());
		
		// retrieve parameters
		AlgoContainerInstance addedAlgo = (AlgoContainerInstance) context.getNewObject();

		ContainerShape contextContainerShape = context.getTargetContainer();
		Object boForContainerShape = getBusinessObjectForPictogramElement(contextContainerShape);
		
		GenlabWorkflowInstance workflow = null;
		if (boForContainerShape instanceof GenlabWorkflowInstance) {
			workflow = (GenlabWorkflowInstance)boForContainerShape;
		} else if (boForContainerShape instanceof IAlgoContainerInstance) {
			IAlgoContainerInstance containerInstance  = (IAlgoContainerInstance)boForContainerShape;
			addedAlgo.setContainer(containerInstance);
			workflow = (GenlabWorkflowInstance) containerInstance.getWorkflow();
		}
		
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
	
		// TODO detect children...
		
		IGaService gaService = Graphiti.getGaService();
		Rectangle invisibleRectangle ;
		RoundedRectangle roundedRectangle;

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
			
			roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED, ROUNDED);
			roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			roundedRectangle.setLineWidth(2);
			roundedRectangle.setBackground(manageColor(IColorConstant.WHITE));

			//roundedRectangle.setFilled(false);

			gaService.setLocationAndSize(
					roundedRectangle, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					0, 
					width, 
					height
					);
			
			// TODO remove
			roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			
		}
		
		/*
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
			
			gaService.setLocationAndSize(
					text, 
					MARGIN_WIDTH, 
					MARGIN_HEIGHT, 
					width-MARGIN_WIDTH, 
					height-MARGIN_HEIGHT
					);
			
			link(shape, addedAlgo);
		}
		*/
		
		 gaService.setLocationAndSize(
         		invisibleRectangle,
         		context.getX(), 
         		context.getY(), 
         		width,
         		height
         		);
		 
		containerShape.setActive(true);
		 
		System.err.println(Graphiti.getPeService().getAllContainedPictogramElements(containerShape));

		
		
        //layoutPictogramElement(containerShape);
	      
		return containerShape;
	}
	
	

}
