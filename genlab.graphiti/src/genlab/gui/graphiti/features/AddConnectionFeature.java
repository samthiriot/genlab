package genlab.gui.graphiti.features;

import genlab.core.model.instance.IConnection;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

// TODO delete
public class AddConnectionFeature extends AbstractAddFeature implements
		IAddFeature {

	public AddConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		
		return context.getNewObject() instanceof IConnection;
		
	}

	@Override
	public PictogramElement add(IAddContext context) {
		
		GLLogger.traceTech("adding a pictogram element for context: "+context, getClass());
		
		IConnection genlabConnection = (IConnection)context.getNewObject();
		
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		Connection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		Polyline polyline = gaService.createPolyline(connection);
		polyline.setStyle(StylesUtils.getStyleForConnection(getDiagram()));
		
		
		// add arrow
		ConnectionDecorator cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd);
		
		link(connection, genlabConnection);

		return connection;
	}
	

	private Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
		
		final Polyline polyline = Graphiti.getGaCreateService().createPolygon(
				gaContainer,
				new int[] {-13, 8, 0, 0, -13, -8 }
				);
		
		polyline.setStyle(StylesUtils.getStyleForConnection(getDiagram()));
		polyline.setFilled(true);
		
		return polyline;
	}



}
