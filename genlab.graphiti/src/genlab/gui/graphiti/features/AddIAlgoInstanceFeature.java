package genlab.gui.graphiti.features;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.GraphitiImageProvider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * From an algo instance, displays it.
 * 
 * TODO manage color constants
 * 
 * @see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fselection-behavior.htm
 * 
 * @author Samuel Thiriot
 *
 */
public class AddIAlgoInstanceFeature extends AddFeatureAbstract {

	public static final int ROUNDED = 20;
	
	
	public AddIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);
		
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		
		// only manage standard algo instance: no container
		if (
				!(context.getNewObject() instanceof IAlgoInstance)
				|| 
				(context.getNewObject() instanceof IAlgoContainerInstance)
				) 
			return false;
		
		IAlgoInstance boAdded = (IAlgoInstance)context.getNewObject();
			
		// don't create the graphics again
		if (getFeatureProvider().getPictogramElementForBusinessObject(context.getNewObject()) != null)
			return false;
		
		// and to what it is added
		Object boTarget = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (boTarget == null) 
			return false;
		
		// can only add an instance into a container
		if (!(boTarget instanceof IAlgoContainerInstance))
			return false;
		
		IAlgoContainerInstance boTargetContainer = (IAlgoContainerInstance)boTarget;
		
		return (
				boTargetContainer.canContain(boAdded)
				&&
				boAdded.canBeContainedInto(boTargetContainer)
				);
				
		
	}

	@Override
	public PictogramElement add(IAddContext context) {

		GLLogger.debugTech("creating an algo graphic representation", getClass());
		
		// retrieve parameters
		AlgoInstance addedAlgo = (AlgoInstance) context.getNewObject();
		
		ContainerShape contextTargetContainer = context.getTargetContainer();
		
		Object boForContainer = getBusinessObjectForPictogramElement(contextTargetContainer);
		GenlabWorkflowInstance workflow = null;
		if (boForContainer instanceof GenlabWorkflowInstance) {
			workflow = (GenlabWorkflowInstance)boForContainer;
		} else if (boForContainer instanceof IAlgoContainerInstance) {
			IAlgoContainerInstance container = (IAlgoContainerInstance)boForContainer;
			workflow = (GenlabWorkflowInstance) container.getWorkflow();
		}
			
		addedAlgo._setWorkflowInstance(workflow);
		
		// create container 
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(contextTargetContainer, true);

		
		// with default size..
		final int width = Math.max(
				context.getWidth(), 
				LayoutIAlgoFeature.MIN_WIDTH
				);
		final int height = Math.max(
				context.getHeight(), 
				LayoutIAlgoFeature.MIN_HEIGHT
				);
		
		IGaService gaService = Graphiti.getGaService();
		RoundedRectangle roundedRectangle;
		Rectangle invisibleRectangle ;

		// create and set graphics algo
		{
			// create invisible rectangle that allows for anchors that appear to be out of the fram
            invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
            gaService.setLocationAndSize(
            		invisibleRectangle,
            		context.getX(), 
            		context.getY(), 
            		width + LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ*2,
            		height
            		);
            
		}
		

		
		// create rounded rect
		{
			roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED, ROUNDED);
			//roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			roundedRectangle.setStyle(StylesUtils.getStyleFor(getDiagram()));

			gaService.setLocationAndSize(
					roundedRectangle, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP, 
					width, 
					height-LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP
					);
			
			// TODO remove
			roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			
			// create link between the domain object and the graphical graphiti representation
			link(containerShape, addedAlgo);
		}
		
		final int lineY = 20 + LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP + LayoutIAlgoFeature.ANCHOR_WIDTH/2;

		// add icon
		{
			String image = addedAlgo.getAlgo().getImagePath16X16();
			if (image != null) {
				final int IMG_WIDTH = 32;
				final int IMG_HEIGHT = 32;
				String id = GraphitiImageProvider.getImageIdForAlgo(addedAlgo.getAlgo());
				Image img = gaService.createImage(invisibleRectangle, id);
				/*gaService.setLocationAndSize(
						img, 
						LayoutIAlgoFeature.ANCHOR_WIDTH/2+5 , 
						6, 
						16, 
						16
						);
						*/
				gaService.setLocationAndSize(
						img, 
						LayoutIAlgoFeature.ANCHOR_WIDTH, 
						0, 
						32, 
						32
						);
			}
		}
		
		// add line
		{
			Shape shape = peCreateService.createShape(containerShape, false);
			Polyline polyline = gaService.createPolyline(shape, new int[] { 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					lineY, 
					width, 
					lineY }
			);
			//polyline.setForeground(manageColor(IColorConstant.DARK_GRAY));
			//polyline.setLineWidth(2);
			polyline.setStyle(StylesUtils.getStyleFor(getDiagram()));

			
		}
		
		
		// add text
		{
			Shape shape = peCreateService.createShape(containerShape, false);
		
			Text text = gaService.createText(shape, addedAlgo.getName());
			//text.setForeground(manageColor(IColorConstant.BLACK));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			//text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
	        text.setStyle(StylesUtils.getStyleForEClassText(getDiagram()));

			gaService.setLocationAndSize(
					text, 
					LayoutIAlgoFeature.TITLE_TEXT_LEFT,
					LayoutIAlgoFeature.ANCHOR_WIDTH/2 + LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP, 
					width-LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ-LayoutIAlgoFeature.ANCHOR_WIDTH-LayoutIAlgoFeature.TITLE_TEXT_LEFT, 
					20
					);
			
			link(shape, addedAlgo);
		}
		
		
		// add connection points
		// .. for inputs
		int yCount = 0;
		int yAnchors = lineY - LayoutIAlgoFeature.ANCHOR_WIDTH;
		int yText = lineY;
		
		for (IInputOutputInstance input: addedAlgo.getInputInstances()) {
			
			// add anchor...
			{	
				createInputEllipse(containerShape, input, yText);
				
			}
			// and text !
			{
				Shape shape = peCreateService.createShape(containerShape, false);
				
				Text text = gaService.createText(shape, input.getMeta().getName());
				//text.setForeground(manageColor(IColorConstant.BLACK));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_BOTTOM);
				//text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
				text.setStyle(StylesUtils.getStyleForEClassText(getDiagram()));
				
				gaService.setLocationAndSize(
						text, 
						LayoutIAlgoFeature.ANCHOR_WIDTH*3/2, 
						yText, 
						width/2, 
						LayoutIAlgoFeature.ANCHOR_WIDTH*2,
						false
						);
				
			}
			
			yAnchors = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH + lineY;
			yCount++;
			yText = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH * 2 + lineY;
		}
		
		// .. for outputs
		yCount = 0;
		yAnchors = lineY - LayoutIAlgoFeature.ANCHOR_WIDTH;
		yText = lineY;
		for (IInputOutputInstance output: addedAlgo.getOutputInstances()) {

			// add anchor...
			{
				createOutputEllipse(containerShape, output, yText, width);
				
				/*
				 * gaService.setLocationAndSize(
						ellipse, 
						width-LayoutIAlgoFeature.ANCHOR_WIDTH*2, 
						yAnchors, 
						LayoutIAlgoFeature.ANCHOR_WIDTH, 
						LayoutIAlgoFeature.ANCHOR_WIDTH
						);
				 */
			}
			
			// and text !
			{
				Shape shape = peCreateService.createShape(containerShape, false);
				
				Text text = gaService.createText(shape, output.getMeta().getName());
				//text.setForeground(manageColor(IColorConstant.BLACK));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
				//text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
				text.setStyle(StylesUtils.getStyleForEClassText(getDiagram()));

				gaService.setLocationAndSize(
						text, 
						width/2, 
						yText, 
						width/2-LayoutIAlgoFeature.ANCHOR_WIDTH, 
						LayoutIAlgoFeature.ANCHOR_WIDTH*2
						);
				
			}
		
			yAnchors = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH + lineY;
			yCount++;
			yText = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH * 2 + lineY;
		}
		
		
		link(containerShape, addedAlgo);
		
	    layoutPictogramElement(containerShape);
	      
		return containerShape;
	}
	
	

}
