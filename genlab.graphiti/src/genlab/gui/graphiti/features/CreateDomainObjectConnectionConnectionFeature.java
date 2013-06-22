package genlab.gui.graphiti.features;

import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;

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
			
			return true;
			
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
		
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		
		// quick exit
		if (context.getTargetAnchor() == null ||context.getSourceAnchor() == null)
			return false;
		
		try {
			GLLogger.traceTech(
							"event can create connection with source and target "+
							context.getSourceAnchor()+
							" and "+
							context.getTargetAnchor(), 
							getClass()
							);

			
			IInputOutputInstance from = (IInputOutputInstance)getBusinessObjectForPictogramElement(context.getSourceAnchor());
			IInputOutputInstance to = (IInputOutputInstance)getBusinessObjectForPictogramElement(context.getTargetAnchor());
			
			return from.getMeta().getType().getId().equals(to.getMeta().getType().getId());
			
		} catch (NullPointerException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		
		GLLogger.traceTech("event create connection with source and target "+context.getSourceAnchor()+" and "+context.getTargetAnchor(), getClass());
		
		IInputOutputInstance from = (IInputOutputInstance)getBusinessObjectForPictogramElement(context.getSourceAnchor());
		IInputOutputInstance to = (IInputOutputInstance)getBusinessObjectForPictogramElement(context.getTargetAnchor());
		
		genlab.core.model.instance.IConnection genlabConnection = from.getAlgoInstance().getWorkflow().connect(from, to);

		// the corresponding graphiti object will be craeted in reaction to the workflow event !

		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(genlabConnection);
		
		Connection c = (Connection) getFeatureProvider().addIfPossible(addContext);
		

		//link(c, genlabConnection);
		
		return c;

	}
}
