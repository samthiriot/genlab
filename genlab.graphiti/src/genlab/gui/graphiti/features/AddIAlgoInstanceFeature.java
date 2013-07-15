package genlab.gui.graphiti.features;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
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
public class AddIAlgoInstanceFeature extends AbstractAddFeature {

	public static final int ROUNDED = 20;
	
	
	public AddIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);
		
	}
	
	@Override
	public boolean canAdd(IAddContext context) {
		
		// check what is added
		if (context.getNewObject() instanceof IAlgoInstance) {
			
			// and to what it is added
			if (context.getTargetContainer() instanceof Diagram) {
				
				IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance) getBusinessObjectForPictogramElement(
						context.getTargetContainer()
						);
				if (workflow == null) {
					GLLogger.warnTech("unable to find the workflow for this diagram, problems ahead", getClass());
					return false;
				}
				
				final IAlgoInstance algoInstanceToAdd = (IAlgoInstance)context.getNewObject();
				
				// don't add the same instance twice, that is...
				return	(
							// the algo instance is not already in the workflow 
							(!workflow.containsAlgoInstance(algoInstanceToAdd))
							||
							// or it still has no graphical representation
							(getFeatureProvider().getPictogramElementForBusinessObject(algoInstanceToAdd) == null)
						);
				
			}
			
		}
		
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {

		GLLogger.debugTech("creating an algo graphic representation", getClass());
		
		// retrieve parameters
		AlgoInstance addedAlgo = (AlgoInstance) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();
		
		GenlabWorkflowInstance workflow = (GenlabWorkflowInstance)this.getBusinessObjectForPictogramElement(targetDiagram);
		
		addedAlgo._setWorkflow(workflow);
		
		// create container 
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		
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
            
			roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED, ROUNDED);
			roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			roundedRectangle.setBackground(manageColor(IColorConstant.WHITE));
			roundedRectangle.setLineWidth(2);

			gaService.setLocationAndSize(
					roundedRectangle, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					0, 
					width, 
					height
					);
			
			// TODO remove
			roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			
			// TODO remove
			//containerShape.setGraphicsAlgorithm(invisibleRectangle);
			
			// create link between the domain object and the graphical graphiti representation
			link(containerShape, addedAlgo);
		}
		
		final int lineY = 20 + LayoutIAlgoFeature.ANCHOR_WIDTH/2;
		
		// add line
		{
			Shape shape = peCreateService.createShape(containerShape, false);
			Polyline polyline = gaService.createPolyline(shape, new int[] { 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					lineY, 
					width, 
					lineY }
			);
			polyline.setForeground(manageColor(IColorConstant.DARK_GRAY));
			polyline.setLineWidth(2);
			
			
		}
		// add text
		{
			Shape shape = peCreateService.createShape(containerShape, false);
		
			Text text = gaService.createText(shape, addedAlgo.getName());
			text.setForeground(manageColor(IColorConstant.BLACK));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			gaService.setLocationAndSize(
					text, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ+LayoutIAlgoFeature.ANCHOR_WIDTH/2, 
					LayoutIAlgoFeature.ANCHOR_WIDTH/2, 
					width-LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ-LayoutIAlgoFeature.ANCHOR_WIDTH/2, 
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
				FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
				anchor.setActive(true);
				anchor.setLocation(gaService.createPoint(0, yText+LayoutIAlgoFeature.ANCHOR_WIDTH/2));
				Ellipse ellipse = gaService.createEllipse(anchor);
				ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
				ellipse.setBackground(manageColor(IColorConstant.WHITE));
				ellipse.setLineWidth(2);
				ellipse.setWidth(LayoutIAlgoFeature.ANCHOR_WIDTH);
				//anchor.setReferencedGraphicsAlgorithm(invisibleRectangle);
				link(anchor, input);
				
				gaService.setLocationAndSize(
						ellipse, 
						0, 0, 
						LayoutIAlgoFeature.ANCHOR_WIDTH, LayoutIAlgoFeature.ANCHOR_WIDTH, 
						false
						);
			}
			// and text !
			{
				Shape shape = peCreateService.createShape(containerShape, false);
				
				Text text = gaService.createText(shape, input.getMeta().getName());
				text.setForeground(manageColor(IColorConstant.BLACK));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_BOTTOM);
				text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
								
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
				FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
				// TODO to explore ? peCreateService.createBoxRelativeAnchor(containerShape);
				anchor.setActive(true);
				anchor.setLocation(
						gaService.createPoint(
								width-LayoutIAlgoFeature.ANCHOR_WIDTH*2, 
								yText+LayoutIAlgoFeature.ANCHOR_WIDTH/2
								)
								);
				Ellipse ellipse = gaService.createEllipse(anchor);
				ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
				ellipse.setBackground(manageColor(IColorConstant.WHITE));
				ellipse.setLineWidth(2);
				ellipse.setWidth(LayoutIAlgoFeature.ANCHOR_WIDTH);
				anchor.setReferencedGraphicsAlgorithm(invisibleRectangle);
				anchor.setGraphicsAlgorithm(ellipse);
	//			anchor.setParent(containerShape);
				link(anchor, output);
	
	
				gaService.setLocationAndSize(
						ellipse, 
						0, 
						0, 
						LayoutIAlgoFeature.ANCHOR_WIDTH, 
						LayoutIAlgoFeature.ANCHOR_WIDTH
						);
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
				text.setForeground(manageColor(IColorConstant.BLACK));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
				text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));

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
