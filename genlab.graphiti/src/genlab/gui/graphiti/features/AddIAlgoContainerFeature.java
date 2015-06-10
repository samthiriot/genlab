package genlab.gui.graphiti.features;

import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.AlgoContainer;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.persistence.AlgoInstanceConverter;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.GraphitiImageProvider;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
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
public class AddIAlgoContainerFeature extends AddFeatureAbstract {

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
		//if (!(context.getTargetContainer() instanceof Diagram))
		//	return false;
				

		IAlgoContainerInstance containerTarget = (IAlgoContainerInstance) getBusinessObjectForPictogramElement(
				context.getTargetContainer()
				);
		if (containerTarget == null) {
			GLLogger.warnTech("unable to find the container for this diagram, problems ahead", getClass());
			return false;
		}

		if (!containerTarget.canContain(aiAdded))
			return false;
		if (!aiAdded.canBeContainedInto(containerTarget))
			return false;
			
		
		// don't add the same instance twice, that is...
		return	(
					// the algo instance is not already in the workflow 
					(!containerTarget.getWorkflow().containsAlgoInstance(aiAdded))
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
				LayoutIAlgoContainerFeature.computeMinimumWidth(addedAlgo)
				);
		int height = Math.max(
				context.getHeight(), 
				LayoutIAlgoContainerFeature.computeMinimumHeight(addedAlgo)
				);
	
		// TODO detect children...
		
		IGaService gaService = Graphiti.getGaService();
		Rectangle invisibleRectangle ;
		RoundedRectangle roundedRectangle;
		RoundedRectangle roundedRectangleInside;


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
		
		// filled rectangle
		{
			roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED, ROUNDED);
			//roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			//roundedRectangle.setLineWidth(2);
			//roundedRectangle.setBackground(manageColor(IColorConstant.WHITE));
			roundedRectangle.setStyle(StylesUtils.getStyleFor(getDiagram()));

			//roundedRectangle.setFilled(false);

			gaService.setLocationAndSize(
					roundedRectangle, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ, 
					LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP, 
					width, 
					height
					);
			
			// TODO remove
			roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			
		}
		

		
		// white rectangle
		{
			roundedRectangleInside = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED, ROUNDED);
			//roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			//roundedRectangle.setLineWidth(2);
			//roundedRectangle.setBackground(manageColor(IColorConstant.WHITE));
			roundedRectangleInside.setStyle(StylesUtils.getStyleForRectangleInside(getDiagram()));

			//roundedRectangle.setFilled(false);

			gaService.setLocationAndSize(
					roundedRectangleInside, 
					// x1
					LayoutIAlgoContainerFeature.RECTANGLE_INSIDE_LEFT
						+ LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_HORIZ
						+(addedAlgo.getInputInstances().isEmpty()? 0:LayoutIAlgoContainerFeature.IO_LABELS_WIDTH), 
					// y1
					LayoutIAlgoContainerFeature.RECTANGLE_INSIDE_TOP, 
					// x2
					width-(addedAlgo.getOutputInstances().isEmpty()?0:LayoutIAlgoContainerFeature.IO_LABELS_WIDTH), 
					// y2
					height-LayoutIAlgoContainerFeature.RECTANGLE_INSIDE_TOP
					);
			
			// TODO remove
			roundedRectangleInside.setParentGraphicsAlgorithm(invisibleRectangle);
			
		}
		
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
		
		final int lineY = LayoutIAlgoContainerFeature.Y_LINE;
		

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
		

		// add connection points
		// .. for inputs
		int yCount = 0;
		int yAnchors = lineY - LayoutIAlgoFeature.ANCHOR_WIDTH;
		int yText = lineY;
		
		for (IInputOutputInstance input: addedAlgo.getInputInstances()) {
			
			// add anchor...
			createInputEllipse(containerShape, input, yText);
			
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
						// x
						LayoutIAlgoFeature.ANCHOR_WIDTH*3/2, 
						// y
						yText,
						// width
						LayoutIAlgoContainerFeature.IO_LABELS_WIDTH,
						// height
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
			createOutputEllipse(containerShape, output, yText, width);
			
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
						// x
						width/2,
						// y
						yText, 
						// width
						LayoutIAlgoContainerFeature.IO_LABELS_WIDTH,
						// height
						LayoutIAlgoFeature.ANCHOR_WIDTH*2
						);
				
			}
		
			yAnchors = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH + lineY;
			yCount++;
			yText = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH * 2 + lineY;
		}
		

		gaService.setLocationAndSize(
	     		invisibleRectangle,
	     		context.getX(), 
	     		context.getY(), 
	     		width,
	     		height
	     		);
		 
		containerShape.setActive(true);
				
		
        //layoutPictogramElement(containerShape);
	      
		return containerShape;
	}
	
	

}
