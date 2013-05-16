package genlab.gui.graphiti.diagram;

import genlab.core.algos.ExistingAlgos;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.features.AddIAlgoConnectionFeature;
import genlab.gui.graphiti.features.CreateDomainObjectConnectionConnectionFeature;
import genlab.gui.graphiti.features.CreateIAlgoFeature;
import genlab.gui.graphiti.genlab2graphiti.GenLabIndependenceSolver;
import genlab.gui.graphiti.patterns.DomainObjectPattern;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;

public class GraphitiFeatureProvider extends DefaultFeatureProviderWithPatterns {

	private final GenLabIndependenceSolver independenceSolver;

	public GraphitiFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		GLLogger.debugTech("Graphiti feature provider instanciated: "+getClass().getCanonicalName(), getClass());
		addPattern(new DomainObjectPattern());
		
		independenceSolver = new GenLabIndependenceSolver();
		setIndependenceSolver(independenceSolver);
		
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {new CreateDomainObjectConnectionConnectionFeature(this)};
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		
		// provide the add connection depending to the type 
		if (context.getNewObject() instanceof IAlgoInstance) {
			return new AddIAlgoConnectionFeature(this);
		}
		
		return super.getAddFeature(context);
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures() {
		
		List<IAlgo> algos = new ArrayList(ExistingAlgos.getExistingAlgos().getAlgos());
		
		ICreateFeature[] res = new ICreateFeature[algos.size()];
		
		// add all the possible algos
		for (int i=0; i<algos.size(); i++) {
			IAlgo algo = algos.get(i);
			res[i] = new CreateIAlgoFeature(this, algo);
		}
		
		return res;
	}

	@Override
	public void link(PictogramElement pictogramElement, Object businessObject) {
		// TODO Auto-generated method stub
		super.link(pictogramElement, businessObject);
	}

	@Override
	public void link(PictogramElement pictogramElement, Object[] businessObjects) {
		// TODO Auto-generated method stub
		super.link(pictogramElement, businessObjects);
	}
	
	
	
}
