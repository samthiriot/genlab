package genlab.gui.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;


public class RemoveIAlgoInstanceFeature extends DefaultRemoveFeature {

	public RemoveIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);
		
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return false;
	}



}
