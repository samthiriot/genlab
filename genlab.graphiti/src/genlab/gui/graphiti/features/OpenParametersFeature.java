package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.GraphitiImageProvider;
import genlab.gui.views.ParametersView;
import genlab.gui.views.ViewHelpers;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * This feature opens the parameters view for algo instances which need it.
 * 
 * @author Samuel Thiriot
 *
 */
public class OpenParametersFeature extends AbstractCustomFeature {

	
	public OpenParametersFeature(IFeatureProvider fp) {
		super(fp);

	}


	@Override
	public String getName() {
		return "parameters";
	}
	
	
	@Override
	public String getDescription() {
		return "edit the parameters of this algo";
	}



	@Override
	public boolean canExecute(ICustomContext context) {

		if (context.getInnerPictogramElement() == null)
			return false;
		
		final Object value = getBusinessObjectForPictogramElement(context.getInnerPictogramElement());
		

		if (value == null)
			return false;
		
		if (!(value instanceof IAlgoInstance))
			return false;
		
		IAlgoInstance algoInstance = (IAlgoInstance)value;
		
		IAlgo algo = algoInstance.getAlgo();
		
		if (algo == null) {
			GLLogger.warnTech("no algo for intance "+algoInstance+"; problems ahead", getClass());
			return false;
		}
		
		
		return !algoInstance.getParameters().isEmpty();
		
	}


	@Override
	public void execute(ICustomContext context) {
		
		GLLogger.debugTech("opening preferences...", getClass());
		
		final IAlgoInstance algoInstance = (IAlgoInstance)getBusinessObjectForPictogramElement(context.getInnerPictogramElement());
		
		if (algoInstance == null)
			return;
		
		ViewHelpers.showParametersFor(algoInstance);

	}

	public String getImageId() {
		
		return GraphitiImageProvider.PARAMETERS_ID;
	}
}
