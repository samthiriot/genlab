package genlab.gui.graphiti.features;

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddBendpointContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.ui.services.IUiLayoutService;

/**
 * Provides a basic layout of a graphiti graph.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class BeautifyDiagramFeature extends AbstractCustomFeature {

	/**
	 * minimal distance between nodes
	 */
	private static final int BENDING_POINT_DISTANCE = 40;
	
	/**
	 * Some value we have to add to shift every Y coordinate, for some unknown reason
	 */
	private static final int SHIFT_Y = 5;
	

	public BeautifyDiagramFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getDescription() {
		return "Make the diagram more beautiful with nicer connections"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return "&Beautify diagram"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context) {

		for (Connection c : getDiagram().getConnections()) {
			
			FreeFormConnection cc = (FreeFormConnection)c;
			
			// should we beautify ?
			
			// ... don't change it if there are already bending points added by the user
			if (cc.getBendpoints().size() > 0)
				continue;
			
			// ... don't process if the connection is screwed up
			final ILocation locStart = GraphitiUi.getUiLayoutService().getLocationRelativeToDiagram(c.getStart());
			final ILocation locEnd = GraphitiUi.getUiLayoutService().getLocationRelativeToDiagram(c.getEnd());
			

			// ... don't change if it is not a left to right connection
			if (locEnd.getX() <= locStart.getX())
				continue;
			
			// ... don't change if there is no difference of Y
			if (Math.abs(locEnd.getY() - locStart.getY()) < 5)
				continue;
			
			// ... don't change it if the connection is not long enough
			if (locEnd.getX() - locStart.getX() < BENDING_POINT_DISTANCE*2)
				continue;
			
			// add bending points
			
			// add a bending point close to beginning
			{
				
				int x = locStart.getX()+BENDING_POINT_DISTANCE;
				int y = locStart.getY()+SHIFT_Y;

				cc.getBendpoints().add(Graphiti.getCreateService().createPoint(x, y, 20, 20));
				
			}
			
			// add a bending point close to end
			{
				
				int x = locEnd.getX()-BENDING_POINT_DISTANCE;
				int y = locEnd.getY()+SHIFT_Y;

				cc.getBendpoints().add(Graphiti.getCreateService().createPoint(x, y, 20, 20));
				
			}
		
		}
		
	}

}
