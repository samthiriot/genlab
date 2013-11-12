package genlab.gui.graphiti.features;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.InputInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.Clipboard;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener.UIInfos;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PasteFeature extends AbstractFeature implements IPasteFeature {

	public PasteFeature(IFeatureProvider fp) {
		super(fp);
	}
	

	public boolean canPaste(IPasteContext context) {
	
		// only support pasting directly in the diagram (nothing else selected)
		// TODO ???
        PictogramElement[] pes = context.getPictogramElements();
        System.err.println(Arrays.toString(pes));
        
        // we can only paste on 1 element
        if (pes.length != 1)
        	return false;
        
        final PictogramElement peTarget = pes[0];
        final Object boTarget = getBusinessObjectForPictogramElement(peTarget);
        
        // can only paste into containers (can be a workflow, a loop, etc...)
        if (!(boTarget instanceof IAlgoContainerInstance))
        	return false;
        
        final IAlgoContainerInstance boTargetContainer = (IAlgoContainerInstance)boTarget;
        //final IAlgoContainerInstance boTargetWorkflow = boTargetContainer.getWorkflow();
        
        // can paste, if all objects on the clipboard are genlab classes
        Object[] fromClipboard = Clipboard.singleton.getObjects();
        if (fromClipboard == null || fromClipboard.length == 0) {
            return false;
        }
        for (Object bo : fromClipboard) {
        	
        	// can only paste algo instances or connections
        	if (!(bo instanceof IAlgoInstance))
              	return false;
        	
        	// can only paste if the container agrees with this kind of containment
        	IAlgoInstance boToPaste = (IAlgoInstance)bo;
        	
        	if (
        			!boTargetContainer.canContain(boToPaste) 
        			|| 
        			!boToPaste.canBeContainedInto(boTargetContainer)
        			) {
        		
        		return false;
        	}
        	
        }
        
        return true;
        
	}
	

	public void paste(IPasteContext context) {
		
		System.err.println("should paste");
		
		  // we already verified, that we paste directly in the diagram
        final PictogramElement[] pes = context.getPictogramElements();
        
        //Diagram diagram = (Diagram) pes[0]; 

        
        // retrieve the container objects
        
        final IAlgoContainerInstance boTargetContainer = (IAlgoContainerInstance)getBusinessObjectForPictogramElement(pes[0]);
        final IGenlabWorkflowInstance boTargetWorkflow = boTargetContainer.getWorkflow();
        
                
        // get the EClasses from the clipboard without copying them
        // (only copy the pictogram element, not the business object)
        // then create new pictogram elements using the add feature
        Object[] objects = Clipboard.singleton.getObjects();
        for (Object object : objects) {
        	System.err.println("should paste: "+object);
        	
    		/*
        	AddContext ac = new AddContext();
            ac.setLocation(0, 0); // for simplicity paste at (0, 0)
            ac.setTargetContainer(diagram);
            addGraphicalRepresentation(ac, object);
            */
        }
        
        // prepare objects for pasting (already adds objects into the container)

		// TODO if (container instanceof IGenlabWorkflowInstance)
		//	workflowCopy = (IGenlabWorkflowInstance)container;
		
		
		LinkedList<IAlgoInstance> copiedAlgos = new LinkedList<IAlgoInstance>();
		LinkedList<IConnection> copiedConnections = new LinkedList<IConnection>();

		// prepare by finding the novel coordinates for paste
		int minX = 5000;
		int minY = 5000;
		for (Object o: objects) {
			UIInfos uiInfos = WorkflowListener.lastInstance.getLastUIParameters(o);
			if (uiInfos == null)
				throw new RuntimeException("no UI infos found for "+o);
			minX = Math.min(minX, uiInfos.x);
			minY = Math.min(minX, uiInfos.y);
		}
		GLLogger.traceTech("found as a minimal coordinates x="+minX+" and y="+minY, getClass());
		GLLogger.traceTech("also, the context provides as coordinates x="+context.getX()+" and y="+context.getY(), getClass());
		
		// start by duplicating the algo instances
		Map<IAlgoInstance,IAlgoInstance> original2copy = new HashMap<IAlgoInstance, IAlgoInstance>(objects.length);
		final int deltaX = context.getX() - minX;
		final int deltaY = context.getY() - minY;
		
		for (Object o: objects) {
		
			GLLogger.debugTech("preparing object instance "+o, getClass());
			
			IAlgoInstance ai = (IAlgoInstance)o;
			
			IAlgoInstance resultInstance = ai.cloneInContext(boTargetWorkflow);

			// transmit the UI that the info for the previous for the original object 
			// are now to be used for the copy
			{
				final UIInfos uiInfos = WorkflowListener.lastInstance.getLastUIParameters(o);
	            WorkflowListener.lastInstance.transmitLastUIParameters(
	    				resultInstance,
	    				new WorkflowListener.UIInfos() {{ 
	    					x = Math.max(0, uiInfos.x + deltaX);
	    					y = Math.max(0, uiInfos.y + deltaY);
	    					width = uiInfos.width;
	    					height = uiInfos.height;
	    					containerShape = pes[0];
	    				}}
	    				);
			}
			
			resultInstance.setContainer(boTargetContainer);
			
			boTargetContainer.addChildren(resultInstance);
			            
			original2copy.put(ai, resultInstance);
			
			copiedAlgos.add(resultInstance);
			
		}
		
		// and add the connections
		
		for (IAlgoInstance original: original2copy.keySet()) {
			
			// add the input connections
			GLLogger.debugTech("copy input connections of "+original, getClass());

			for (IConnection cInOrig : original.getAllIncomingConnections()) {
				
				IAlgoInstance fromOrigin = cInOrig.getFrom().getAlgoInstance();
				IAlgoInstance fromCopy = original2copy.get(fromOrigin);
				if (fromCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IAlgoInstance toOrigin = cInOrig.getTo().getAlgoInstance();
				IAlgoInstance toCopy = original2copy.get(toOrigin);
				if (toCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IInputOutputInstance outputInstanceCopy = fromCopy.getOutputInstanceForOutput(cInOrig.getFrom().getMeta());
				if (outputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for output "+cInOrig.getFrom().getMeta());
				
				IInputOutputInstance inputInstanceCopy = toCopy.getInputInstanceForInput(cInOrig.getTo().getMeta());
				if (inputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for input "+cInOrig.getTo().getMeta());
				
				IConnection cCopy = boTargetWorkflow.connect(outputInstanceCopy, inputInstanceCopy);
			
				copiedConnections.add(cCopy);

			}

			
		}
        
	}
	
	@Override
	public boolean canExecute(IContext context) {
		boolean ret = false;
		if (context instanceof IPasteContext) {
			ret = canPaste((IPasteContext) context);
		}
		return ret;
	}

	@Override
	public void execute(IContext context) {
		if (context instanceof IPasteContext) {
			paste((IPasteContext) context);
		}
	}


}
