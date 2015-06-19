package genlab.gui.graphiti.features;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.InputInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.UIUtils;
import genlab.gui.graphiti.Utils;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.impl.EllipseImpl;
import org.eclipse.graphiti.mm.algorithms.impl.TextImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * Updates algo instances; notably updates the name
 * 
 * @author Samuel Thiriot
 *
 */
public class AlgoUpdateFeature extends AbstractUpdateFeature {

	public AlgoUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {

		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		if (bo == null)
			return false;
		
		if (!(bo instanceof IAlgoInstance))
			return false;
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		return !(ai.getAlgo() instanceof IConstantAlgo);
		
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		
		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		// check name
		{
			String displayedName = null;
			
			if (pe instanceof ContainerShape) {
	            ContainerShape cs = (ContainerShape) pe;
	            for (Shape shape : cs.getChildren()) {
	                if (shape.getGraphicsAlgorithm() instanceof Text) {
	                    Text text = (Text) shape.getGraphicsAlgorithm();
	                    displayedName = text.getValue();
	                    break;
	                }
	            }
	        }
			
			if (displayedName != null && !displayedName.equals(ai.getName()))
		            return Reason.createTrueReason("Name is out of date");
		}
		
		// check inputs
		{		
			// there should be a graphical counterpart to each input
			for (IInputOutputInstance input: ai.getInputInstances()) {
				PictogramElement peInput = getFeatureProvider().getPictogramElementForBusinessObject(input);
				if (peInput == null) {
					return Reason.createTrueReason("inputs were added");
				}
			}
			// also there should be no graphical counterpart for no input
			ContainerShape containerShape = (ContainerShape)pe;
			Collection<IInputOutputInstance> aiInputs = ai.getInputInstances();
			List<Anchor> existingInputPE = collectInputs(containerShape);
			for (Anchor a: existingInputPE) {
				Object boInput = getBusinessObjectForPictogramElement(a);
				if (boInput == null || !aiInputs.contains(boInput))
					return Reason.createTrueReason("inputs were removed");
			}
		}
		
		
		
		// check outputs
		{
			// there should be a graphical counterpart to each output
			for (IInputOutputInstance output: ai.getOutputInstances()) {
				PictogramElement peOutput = getFeatureProvider().getPictogramElementForBusinessObject(output);
				if (peOutput == null) {
					return Reason.createTrueReason("outputs were added");
				}
			}
			// also there should be no graphical counterpart for no output
			ContainerShape containerShape = (ContainerShape)pe;
			Collection<IInputOutputInstance> aiOutputs = ai.getOutputInstances();
			List<Anchor> existingOutputPE = collectOutputs(containerShape);
			for (Anchor a: existingOutputPE) {
				Object boOutput = getBusinessObjectForPictogramElement(a);
				if (boOutput == null || !aiOutputs.contains(boOutput))
					return Reason.createTrueReason("outputs were removed");
			}
		}
		
		
		return Reason.createFalseReason();
		
	}
	

	protected List<Anchor> collectInputs(ContainerShape cs) {
		
		List<Anchor> anchors = new LinkedList<Anchor>();
		for (Anchor a: cs.getAnchors()) {
			if (((FixPointAnchor)a).getLocation().getX() == 0) 
				anchors.add(a);
		}
		return anchors;
		
	}

	protected List<Anchor> collectOutputs(ContainerShape cs) {
		
		final int targetX = cs.getGraphicsAlgorithm().getWidth();
		List<Anchor> anchors = new LinkedList<Anchor>();
		for (Anchor a: cs.getAnchors()) {
			if (((FixPointAnchor)a).getLocation().getX() > targetX*2/3) 
				anchors.add(a);
		}
		return anchors;
		
	}
	
	protected Shape getTextForInput(Anchor a, ContainerShape cs) {
		
		int targetY = ((FixPointAnchor)a).getLocation().getY();
		GLLogger.traceTech("searching for the text for anchor "+targetY+" : "+a, getClass());
		
		for (Shape s: cs.getChildren()) {
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			if (Math.abs(targetY - ga.getY() - LayoutIAlgoFeature.ANCHOR_WIDTH/2) < 8  
					&& ga.getX() < cs.getGraphicsAlgorithm().getWidth()/3 
					&& (ga instanceof Text) 
					) 
				return s;
		}
		return null;
		
	}
	

	protected Shape getTextForOutput(Anchor a, ContainerShape cs) {
		
		int targetY = ((FixPointAnchor)a).getLocation().getY();
		GLLogger.traceTech("searching for the text for anchor "+targetY+" : "+a, getClass());
		
		for (Shape s: cs.getChildren()) {
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			if (Math.abs(targetY - ga.getY() - LayoutIAlgoFeature.ANCHOR_WIDTH/2) < 8  
					&& ga.getX() > cs.getGraphicsAlgorithm().getWidth()/3 
					&& (ga instanceof Text) 
					) 
				return s;
		}
		return null;
		
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

	
	@Override
	public boolean update(IUpdateContext context) {

		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		// replace name (first occurence of text)
		boolean replacedName = false;
		{			
			if (pe instanceof ContainerShape) {
	            ContainerShape cs = (ContainerShape) pe;
	            for (Shape shape : cs.getChildren()) {
	                if (shape.getGraphicsAlgorithm() instanceof Text) {
	                    Text text = (Text) shape.getGraphicsAlgorithm();
	                    UIUtils.setValueInTransaction(text, ai.getName());
	                    replacedName = true;
	                    break;
	                }
	            }
	        }
			
		}
		
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		final int lineY = 20 + LayoutIAlgoFeature.INVISIBLE_RECT_MARGIN_TOP + LayoutIAlgoFeature.ANCHOR_WIDTH/2;

		final ContainerShape containerShape = (ContainerShape)pe;
		Collection<IInputOutputInstance> aiInputs = ai.getInputInstances();

		final int width = containerShape.getGraphicsAlgorithm().getWidth();
		
		// now, maybe there are too many inputs ?
		List<Anchor> existingInputPE = collectInputs(containerShape);
		for (Anchor a: existingInputPE) {
			Object boInput = getBusinessObjectForPictogramElement(a);
			if (boInput != null && aiInputs.contains(boInput))
				continue;
			GLLogger.traceTech("will delete an input representation which is not usefull anymore", getClass());
			Shape text = getTextForInput(a, containerShape);
			if (text == null) {
				GLLogger.warnTech("unable to find the text corresponding to this input", getClass());
			} else {
				Graphiti.getPeService().deletePictogramElement(text);
			}
			Graphiti.getPeService().deletePictogramElement(a);
		}
		
		// check anchors
		int yCount = 0;
		int yAnchors = lineY - LayoutIAlgoFeature.ANCHOR_WIDTH;
		int yText = lineY;
		
		
		for (IInputOutputInstance input: aiInputs) {
			
			// if this input already has a counterpart
			PictogramElement peAnchor = getFeatureProvider().getPictogramElementForBusinessObject(input);
			if (peAnchor != null) {
				// just check position and text
				//peAnchor.getGraphicsAlgorithm().setX(0);	
				Shape text = getTextForInput((Anchor) peAnchor, containerShape);
				if (text != null) {
					text.getGraphicsAlgorithm().setY(yText);
				}
				((FixPointAnchor)peAnchor).getLocation().setY(yText+LayoutIAlgoFeature.ANCHOR_WIDTH/2);

			} else {
			
				// ahah, this was not existing yet !
				GLLogger.traceTech("there was no counterpart for input "+input.getName()+", let's create it", getClass());
				
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
			}
			yAnchors = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH + lineY;
			yCount++;
			yText = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH * 2 + lineY;
		}
		
		// now, maybe there are too many outputs ?
		List<Anchor> existingOutputPE = collectOutputs(containerShape);
		Collection<IInputOutputInstance> aiOutputs = ai.getOutputInstances();
		for (Anchor a: existingOutputPE) {
			Object boOutput = getBusinessObjectForPictogramElement(a);
			if (boOutput != null && aiOutputs.contains(boOutput))
				continue;
			GLLogger.traceTech("will delete an output representation which is not usefull anymore", getClass());
			Shape text = getTextForOutput(a, containerShape);
			if (text == null) {
				GLLogger.warnTech("unable to find the text corresponding to this output", getClass());
			} else {
				Graphiti.getPeService().deletePictogramElement(text);
			}
			Graphiti.getPeService().deletePictogramElement(a);
		}
		
		// check anchors
		yCount = 0;
		yAnchors = lineY - LayoutIAlgoFeature.ANCHOR_WIDTH;
		yText = lineY;
		
		for (IInputOutputInstance output: aiOutputs) {
			
			// if this input already has a counterpart
			PictogramElement peAnchor = getFeatureProvider().getPictogramElementForBusinessObject(output);
			if (peAnchor != null) {
				// just check position and text
				Shape text = getTextForOutput((Anchor) peAnchor, containerShape);
				if (text != null) {
					text.getGraphicsAlgorithm().setY(yText);
				}
				((FixPointAnchor)peAnchor).getLocation().setY(yText+LayoutIAlgoFeature.ANCHOR_WIDTH/2);

			} else {
			
				// ahah, this was not existing yet !
				GLLogger.traceTech("there was no counterpart for output "+output.getName()+", let's create it", getClass());
				
				// add anchor...
				{	
					// TODO width ? 
					createOutputEllipse(containerShape, output, yText, width);
					
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
			}
			yAnchors = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH + lineY;
			yCount++;
			yText = yCount*LayoutIAlgoFeature.ANCHOR_WIDTH * 2 + lineY;
		}
		
		return replacedName;
	}

}
