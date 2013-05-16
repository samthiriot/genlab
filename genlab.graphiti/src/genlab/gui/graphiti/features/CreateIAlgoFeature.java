package genlab.gui.graphiti.features;

import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoInstance;
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
public class CreateIAlgoFeature extends AbstractCreateFeature {

	private final IAlgo algo;
	
	public CreateIAlgoFeature(IFeatureProvider fp, IAlgo algo) {
		super(fp, algo.getName(), algo.getDescription());
		this.algo = algo;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
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
		GLLogger.debugTech("should add the algo instance therre ????", getClass());

		IAlgoInstance algoInstance = algo.createInstance();
		
		addGraphicalRepresentation(context, algoInstance);

		return new Object[]{algoInstance};
		
		
	}

}
