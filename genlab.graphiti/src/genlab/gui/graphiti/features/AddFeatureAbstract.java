package genlab.gui.graphiti.features;

import genlab.core.model.instance.IInputOutputInstance;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

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
public abstract class AddFeatureAbstract extends AbstractAddFeature {

	public AddFeatureAbstract(IFeatureProvider fp) {
		super(fp);
		
	}
	
	protected Ellipse createInputEllipse(ContainerShape containerShape, IInputOutputInstance input, int yBase) {

		final IGaService gaService = Graphiti.getGaService();
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();

		FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
		anchor.setActive(true);
		anchor.setLocation(
				gaService.createPoint(
				0, 
				yBase+LayoutIAlgoFeature.ANCHOR_WIDTH/2
				)
				);
	
		Ellipse ellipse = gaService.createEllipse(anchor);
		//ellipse.setForeground(manageColor(IColorConstant.DARK_GRAY));
		//ellipse.setBackground(manageColor(IColorConstant.WHITE));
		//ellipse.setLineWidth(2);
		ellipse.setWidth(LayoutIAlgoFeature.ANCHOR_WIDTH);
		ellipse.setStyle(StylesUtils.getStyleFor(getDiagram()));
		//anchor.setReferencedGraphicsAlgorithm(invisibleRectangle);
		link(anchor, input);

		gaService.setLocationAndSize(
				ellipse, 
				0, 0, 
				LayoutIAlgoFeature.ANCHOR_WIDTH, LayoutIAlgoFeature.ANCHOR_WIDTH, 
				false
				);
		
		return ellipse;
	}


	protected Ellipse createOutputEllipse(ContainerShape containerShape, IInputOutputInstance output, int yBase, int width) {

		final IGaService gaService = Graphiti.getGaService();
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();


		FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
		// TODO to explore ? peCreateService.createBoxRelativeAnchor(containerShape);
		anchor.setActive(true);
		anchor.setLocation(
				gaService.createPoint(
						width-LayoutIAlgoFeature.ANCHOR_WIDTH,//*2, 
						yBase+LayoutIAlgoFeature.ANCHOR_WIDTH/2
						)
						);
		Ellipse ellipse = gaService.createEllipse(anchor);
	
		ellipse.setWidth(LayoutIAlgoFeature.ANCHOR_WIDTH);
		
		if (output.getMeta().isContinuousOutput())
			ellipse.setStyle(StylesUtils.getStyleForOutputContinuous(getDiagram()));
		else
			ellipse.setStyle(StylesUtils.getStyleForOutputOneshot(getDiagram()));

		//anchor.setReferencedGraphicsAlgorithm(invisibleRectangle);
		anchor.setGraphicsAlgorithm(ellipse);
		
		link(anchor, output);

		gaService.setLocationAndSize(
				ellipse, 
				0, 
				0, 
				LayoutIAlgoFeature.ANCHOR_WIDTH, 
				LayoutIAlgoFeature.ANCHOR_WIDTH
				);
		
		return ellipse;
	}
}
