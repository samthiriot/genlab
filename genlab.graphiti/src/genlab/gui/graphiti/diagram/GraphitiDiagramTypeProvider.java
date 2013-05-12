package genlab.gui.graphiti.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;

public class GraphitiDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public GraphitiDiagramTypeProvider() {
		super();
		setFeatureProvider(new GraphitiFeatureProvider(this));
	}
	
	
}
