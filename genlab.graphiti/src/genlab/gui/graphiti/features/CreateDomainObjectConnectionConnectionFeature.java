package genlab.gui.graphiti.features;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.Utils;
import genlab.gui.graphiti.editors.IntuitiveObjectCreation;
import genlab.gui.graphiti.editors.IntuitiveObjectCreation.ProposalObjectCreation;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;

public class CreateDomainObjectConnectionConnectionFeature extends AbstractCreateConnectionFeature
		implements ICreateConnectionFeature {

	public CreateDomainObjectConnectionConnectionFeature(IFeatureProvider fp) {
		super(fp, "connection", "create a connection");
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		
		// TODO: check for right domain object instance below
		// return getBusinessObjectForPictogramElement(context.getSourcePictogramElement()) instanceof <DomainObject>;

		GLLogger.traceTech("event can start connection with source "+context.getSourceAnchor(), getClass());
		try {
			IInputOutputInstance from = (IInputOutputInstance)getBusinessObjectForPictogramElement(context.getSourceAnchor());
			
			return (from != null);
			
			
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
		
	}

	

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		
		// two cases:
		// - source and dest were both filled: standard creation of a connection
		// - only source provided: source should be used as dest or origin (depending on its type.
		
		// quick exit
		// nota: we accept strange cases with source empty. 
		// The idea is to propose dynamically the creation of something
		
		// if no source, we can do nothing
		if (context.getSourceAnchor() == null)
			return false;
		
		
		try {
			GLLogger.traceTech(
							"event can create connection with source and target "+
							context.getSourceAnchor()+
							" and "+
							context.getTargetAnchor(), 
							getClass()
							);

	
			// there is always a source (but it may become a dest !)
			Anchor source = context.getSourceAnchor();
			IInputOutputInstance from = (IInputOutputInstance)getBusinessObjectForPictogramElement(source);
			
			Anchor dest = context.getTargetAnchor();
			IInputOutputInstance to = null;
			if (dest != null) {
				to = (IInputOutputInstance)getBusinessObjectForPictogramElement(dest);
			}
			
			if (to != null) {
				
				// standard case: ensure the user attempts to really connect 
				// outputs to inputs
				
				// correct wrong direction if necceary
				IInputOutputInstance correctedFrom = null;
				IInputOutputInstance correctedTo = null;
				boolean fromIsOutput = from.getAlgoInstance().getOutputInstances().contains(from);
				boolean destIsInput = to.getAlgoInstance().getInputInstances().contains(to);
				
				
				if (fromIsOutput) {
					if (destIsInput) {
						// no correction to do :-)
						correctedFrom = from;
						correctedTo = to;	
					} else {
						// strange demand...
						return false;
					}
				} else {
					if (!destIsInput) {
						// correction !
						correctedFrom = to;
						correctedTo = from;
					} else {
						// strange demand
						return false;
					}
				}
				
				return correctedFrom.acceptsConnectionTo(correctedTo) && to.acceptsConnectionFrom(correctedTo);
				
			} else {
				
				// no standard case: dest == null, probably the user wants us to help him.
				// engage in a suggestion process: given the destination, what can we provide as an input ?
				ProposalObjectCreation proposal = IntuitiveObjectCreation.getAutoInputForOutput(from.getMeta());
				
				// no proposal => can no create
				return (proposal != null); 
				
			}
	
			
			
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public Connection create(final ICreateConnectionContext context) {
		
		GLLogger.traceTech("event create connection with source and target "+context.getSourceAnchor()+" and "+context.getTargetAnchor(), getClass());

		Anchor source = context.getSourceAnchor();
		IInputOutputInstance from = (IInputOutputInstance)getBusinessObjectForPictogramElement(source);
		
		Anchor dest = context.getTargetAnchor();
		IInputOutputInstance to = null;
		if (dest != null)
			to = (IInputOutputInstance)getBusinessObjectForPictogramElement(dest);
		
		IInputOutputInstance correctedFrom = null;
		IInputOutputInstance correctedTo = null;
		
		if (dest != null) {
			// standard case
		
			// correct wrong direction if necceary
			boolean fromIsOutput = from.getAlgoInstance().getOutputInstances().contains(from);
			boolean destIsInput = to.getAlgoInstance().getInputInstances().contains(to);
			
			if (fromIsOutput) {
				if (destIsInput) {
					// no correction to do :-)
					correctedFrom = from;
					correctedTo = to;	
				} else {
					// strange demand...
					return null;
				}
			} else {
				if (!destIsInput) {
					// correction !
					correctedFrom = to;
					correctedTo = from;
				} else {
					// strange demand...
					return null;
				}
			}
		} else {
			
			GLLogger.debugTech("no dest provided; will search something to add as an input", getClass());
			
			correctedTo = from;
			correctedFrom = null;
					
			// engage in a suggestion process: given the destination, what can we provide as an input ?
			ProposalObjectCreation proposal = IntuitiveObjectCreation.getAutoInputForOutput(correctedTo.getMeta());
			
			// no proposal => can no create
			if (proposal == null) {
				GLLogger.warnTech("unable to create this connection, no source was automatically found", getClass());
				return null; 
			}
			
			// apply the proposal: create the corresponding algo instance
			{
				GLLogger.debugTech("applying the proposal", getClass());
					
				IGenlabWorkflowInstance workflow = from.getAlgoInstance().getWorkflow();
				
				IAlgoInstance addInstance = proposal.algoToCreate.createInstance(workflow);
				
				final FixPointAnchor anchorToFixed = (FixPointAnchor)source;

				WorkflowListener.lastInstance.transmitLastUIParameters(
						addInstance, 
						new WorkflowListener.UIInfos() {{
							x = context.getTargetLocation().getX() - 100;
							y = context.getTargetLocation().getY();
							width = 100;
							height = 100;
						}}
						);
				
				// add this instance to the workflow, this will create the corresponding graphical representation
				workflow.addAlgoInstance(addInstance);
				
				// then create the connection
				correctedFrom = addInstance.getOutputInstanceForOutput(proposal.ioToUse);
				
				// this should create the corresponding graphical representation
			}
			
			
		}
		
		genlab.core.model.instance.IConnection genlabConnection = from.getAlgoInstance().getWorkflow().connect(
				correctedFrom, 
				correctedTo
				);

		// the corresponding graphiti object will be craeted in reaction to the workflow event !
				
		//AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		//addContext.setNewObject(genlabConnection);
		
		//Connection c = (Connection) getFeatureProvider().addIfPossible(addContext);
		

		//link(c, genlabConnection);
		
		return null;

	}

	@Override
	public void canceledAttaching(ICreateConnectionContext context) {
		// TODO Auto-generated method stub
		super.canceledAttaching(context);
		GLLogger.warnTech("cancelled attaching : "+context, getClass());
		
	}
	
	
	
}
