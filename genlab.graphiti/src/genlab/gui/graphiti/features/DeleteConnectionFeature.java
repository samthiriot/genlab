package genlab.gui.graphiti.features;

import java.net.ConnectException;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

/**
 * Feature to delete a connection
 * 
 * @author Samuel Thiriot
 *
 */
public class DeleteConnectionFeature extends DefaultDeleteFeature {

	public DeleteConnectionFeature(IFeatureProvider fp) {
		super(fp);

	}

	@Override
	public void delete(IDeleteContext context) {
		
		GLLogger.debugTech("Connection removed from the diagram, removing it from genlab as well", getClass());

		Connection c = (Connection) getBusinessObjectForPictogramElement(context.getPictogramElement());

		// delete graphiti objects
		//super.delete(context);
		
		
		// delete the corresponding IAlgoInstance
		c.getFrom().getAlgoInstance().getWorkflow().removeConnection(c);
		
		// the corresponding graphical elements are supposed to be removed by a callback
		
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		
		if (context.getPictogramElement() == null)
			return false;
		
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		return (bo != null && (bo instanceof Connection));
		
		
	}

	
	
	
}
