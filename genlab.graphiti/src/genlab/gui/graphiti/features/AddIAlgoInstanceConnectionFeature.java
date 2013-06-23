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
 * @author Samuel Thiriot
 *
 */
public class AddIAlgoInstanceConnectionFeature extends AbstractAddFeature {

	public static final int ANCHOR_WIDTH = 10;
	public static final int ROUNDED = 20;
	
	
	public AddIAlgoInstanceConnectionFeature(IFeatureProvider fp) {
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
		final int width = 100;
		final int height = 100;
		IGaService gaService = Graphiti.getGaService();
		RoundedRectangle roundedRectangle;
		
		// create and set graphics algo
		{
			roundedRectangle = gaService.createRoundedRectangle(containerShape, ROUNDED, ROUNDED);
			System.err.println("created rectangle: "+roundedRectangle);
			roundedRectangle.setForeground(manageColor(IColorConstant.DARK_GRAY));
			roundedRectangle.setBackground(manageColor(IColorConstant.WHITE));
			roundedRectangle.setLineWidth(2);
			gaService.setLocationAndSize(
					roundedRectangle, 
					context.getX(), context.getY(), 
					width, height
					);
			containerShape.setGraphicsAlgorithm(roundedRectangle);

			// create link between the domain object and the graphical graphiti representation
			link(containerShape, addedAlgo);
		}
		
		// add line
		{
			Shape shape = peCreateService.createShape(containerShape, false);
			Polyline polyline = gaService.createPolyline(shape, new int[] { 0, 20, width, 20 });
			polyline.setForeground(manageColor(IColorConstant.DARK_GRAY));
			polyline.setLineWidth(2);
			
			
		}
		// add text
		{
			Shape shape = peCreateService.createShape(containerShape, false);
		
			Text text = gaService.createText(shape, addedAlgo.getAlgo().getName());
			text.setForeground(manageColor(IColorConstant.BLACK));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			gaService.setLocationAndSize(text, 0, 0, width, 20);
			
			link(shape, addedAlgo);
		}
		
		// add connection points
		// .. for inputs
		int y = 20;
		for (IInputOutputInstance input: addedAlgo.getInputInstances()) {
			FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
			anchor.setActive(true);
			anchor.setLocation(gaService.createPoint(-ANCHOR_WIDTH/2, y));
			Ellipse ellipse = gaService.createEllipse(anchor);
			ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
			ellipse.setBackground(manageColor(IColorConstant.WHITE));
			ellipse.setLineWidth(2);
			ellipse.setWidth(ANCHOR_WIDTH);
			anchor.setReferencedGraphicsAlgorithm(roundedRectangle);
			//anchor.setGraphicsAlgorithm(ellipse);
			//anchor.setParent(containerShape);
			link(anchor, input);
			
			gaService.setLocationAndSize(ellipse, 0, y, ANCHOR_WIDTH, ANCHOR_WIDTH);

			y += 10;
		}
		// .. for outputs
		y = 20;
		for (IInputOutputInstance output: addedAlgo.getOutputInstances()) {
			FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
			anchor.setActive(true);
			anchor.setLocation(gaService.createPoint(width-ANCHOR_WIDTH/2, y));
			Ellipse ellipse = gaService.createEllipse(anchor);
			ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
			ellipse.setBackground(manageColor(IColorConstant.WHITE));
			ellipse.setLineWidth(2);
			ellipse.setWidth(ANCHOR_WIDTH);
			anchor.setReferencedGraphicsAlgorithm(roundedRectangle);
			//anchor.setGraphicsAlgorithm(ellipse);
//			anchor.setParent(containerShape);
			link(anchor, output);


			gaService.setLocationAndSize(ellipse, width-ANCHOR_WIDTH, y, ANCHOR_WIDTH, ANCHOR_WIDTH);

			y += 10;
		}
		
		
		link(containerShape, addedAlgo);
		
	    layoutPictogramElement(containerShape);
	      
		return containerShape;
	}
	
	

}
