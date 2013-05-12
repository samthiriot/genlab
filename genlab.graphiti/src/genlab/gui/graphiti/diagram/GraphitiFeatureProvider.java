package genlab.gui.graphiti.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;

import genlab.gui.graphiti.features.AddDomainObjectConnectionConnectionFeature;
import genlab.gui.graphiti.features.CreateDomainObjectConnectionConnectionFeature;
import genlab.gui.graphiti.patterns.DomainObjectPattern;


public class GraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	public GraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		addPattern(new DomainObjectPattern());
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {new CreateDomainObjectConnectionConnectionFeature(this)};
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// TODO: check for right domain object instances below
		if (context instanceof IAddConnectionContext /* && context.getNewObject() instanceof DomainObjectConnection */) {
			return new AddDomainObjectConnectionConnectionFeature(this);
		}
		return super.getAddFeature(context);
	}
}
