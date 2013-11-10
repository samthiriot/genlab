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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        if (pes.length != 1 || !(pes[0] instanceof Diagram)) {
            return false;
        }
        
        // can paste, if all objects on the clipboard are genlab classes
        Object[] fromClipboard = Clipboard.singleton.getObjects();
        if (fromClipboard == null || fromClipboard.length == 0) {
            return false;
        }
        for (Object bo : fromClipboard) {
        	  if (
              		!(bo instanceof IAlgoInstance)
              		&&
              		!(bo instanceof IConnection)
              		)
              	return false;
        }
        
        return true;
        
	}
	
	// TODO something else than diagram for container
	protected Collection<IAlgoInstance> prepareForPaste(Diagram diagram, IAlgoContainerInstance container, Object[] os) {
		
		IGenlabWorkflowInstance workflowCopy = null;
		if (container instanceof IGenlabWorkflowInstance)
			workflowCopy = (IGenlabWorkflowInstance)container;
		
		
		LinkedList<IAlgoInstance> copiedAlgos = new LinkedList<IAlgoInstance>();
		LinkedList<IConnection> copiedConnections = new LinkedList<IConnection>();

		// start by duplicating the algo instances
		Map<IAlgoInstance,IAlgoInstance> original2copy = new HashMap<IAlgoInstance, IAlgoInstance>(os.length);
		for (Object o: os) {
		
			GLLogger.debugTech("preparing object instance "+o, getClass());
			
			IAlgoInstance ai = (IAlgoInstance)o;
			
			IAlgoInstance resultInstance = ai.cloneInContext(container);
			
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
				
				IConnection cCopy = workflowCopy.connect(outputInstanceCopy, inputInstanceCopy);
			
				copiedConnections.add(cCopy);

			}

			
		}
		
		// create graphical representations
		
		/*
		// .. algos
		Map<IAlgoInstance,PictogramElement> algoInstance2pe = new HashMap<IAlgoInstance, PictogramElement>(os.length);
		for (IAlgoInstance createdInstance : copiedAlgos) {
			
			AddContext ac = new AddContext();
	        ac.setLocation(0, 0); // for simplicity paste at (0, 0)
	        ac.setTargetContainer(diagram);
	        
	        PictogramElement pe = addGraphicalRepresentation(ac, createdInstance);
	        algoInstance2pe.put(createdInstance, pe);
	       
		}
    	*/
		// .. connections
		/*
		for (IConnection c : copiedConnections) {
			
			Anchor anchorFrom = (Anchor)getFeatureProvider().getPictogramElementForBusinessObject(c.getFrom());
			Anchor anchorTo = (Anchor)getFeatureProvider().getPictogramElementForBusinessObject(c.getTo());
			
			if (anchorFrom == null || anchorTo == null)
				throw new ProgramException("unable to find anchors to create the connection "+c);
			
			AddConnectionContext ac = new AddConnectionContext(anchorFrom, anchorTo);
			
	        ac.setLocation(0, 0); // for simplicity paste at (0, 0)
	        ac.setTargetContainer(diagram);
	        
	        //getFeatureProvider();
	        //addGraphicalRepresentation(ac, c);
	        
	        
		}
        	*/	
		return copiedAlgos;
	}

	public void paste(IPasteContext context) {
		
		System.err.println("should paste");
		
		  // we already verified, that we paste directly in the diagram
        PictogramElement[] pes = context.getPictogramElements();
        Diagram diagram = (Diagram) pes[0]; 
        
        // retrieve the container objects
        IGenlabWorkflowInstance workflow = (IGenlabWorkflowInstance)getBusinessObjectForPictogramElement(diagram);
        
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
        prepareForPaste(diagram, workflow, objects);
           
        
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
