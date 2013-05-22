package genlab.gui.graphiti.features;

import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * For an algo taken from the palette, creates an instance in the diagram.
 * 
 * @author Samuel Thiriot
 *
 */
public class CreateIAlgoInstanceFeature extends AbstractCreateFeature {

	private final IAlgo algo;
	
	public CreateIAlgoInstanceFeature(IFeatureProvider fp, IAlgo algo) {
		super(fp, algo.getName(), algo.getDescription());
		this.algo = algo;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		
		if (!(context.getTargetContainer() instanceof Diagram))
			return false;
		
		return true;
		
	}

	@Override
	public Object[] create(ICreateContext context) {
		/*
		// ask for the algo name
		String newClassName = UIUtils.askString(
				"name of this algo ?", 
				"truc", 
				"");
		// ... valid ? 
		if (newClassName == null || newClassName.trim().length() == 0) {
			return EMPTY;
		}
		*/
		
		// create instance
		GLLogger.debugTech("adding an algo instance for algo "+algo.getName(), getClass());

		// TODO should add this to the workflow !
		
		System.err.println("search for diag: "+context.getTargetContainer());
		IGenlabWorkflow workflow = (IGenlabWorkflow)getBusinessObjectForPictogramElement(context.getTargetContainer());
		if (workflow == null)
			GLLogger.warnTech("unable to find the workflow related to this diagram, problems ahead", getClass());
		
		IAlgoInstance algoInstance = algo.createInstance(workflow);
		
		addGraphicalRepresentation(context, algoInstance);

		
		return new Object[]{algoInstance};
		
		
	}
	
	public IAlgo getAlgo() {
		return algo;
	}

}
