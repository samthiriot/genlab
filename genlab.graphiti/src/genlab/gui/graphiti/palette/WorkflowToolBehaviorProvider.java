package genlab.gui.graphiti.palette;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.features.CreateIAlgoInstanceFeature;
import genlab.gui.graphiti.features.OpenParametersFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.internal.Messages;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextEntryHelper;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.eclipse.graphiti.tb.IContextButtonPadData;

public class WorkflowToolBehaviorProvider extends DefaultToolBehaviorProvider {

	protected static int CONTEXT_BUTTON_PARAMETERS = 1 << 4;

	public WorkflowToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	
	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		
		IContextButtonPadData data = super.getContextButtonPad(context);
		
		PictogramElement pe = context.getPictogramElement();
	
		// add default buttons 
		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);

		// setting behavior
	
		
		// add the "parameters" feature to the generic pad
		{
			CustomContext cc = new CustomContext(new PictogramElement[] {pe});
			cc.setInnerPictogramElement(pe);
			
			OpenParametersFeature feature = new OpenParametersFeature(getFeatureProvider());
			ContextButtonEntry button = new ContextButtonEntry(feature, cc);
			button.setText(feature.getName());
			button.setDescription(feature.getDescription());
			button.setIconId(feature.getImageId());
			data.getGenericContextButtons().add(0, button);

		}
		
		
		return data;
	}


	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> res = new ArrayList<IPaletteCompartmentEntry>();
		
		// add compartments from super class
		{
			IPaletteCompartmentEntry[] superCompartments = super.getPalette();
			for (int i = 0; i < superCompartments.length; i++) {
				if (!superCompartments[i].getLabel().equals(Messages.DefaultToolBehaviorProvider_1_xfld))
					res.add(superCompartments[i]);
			}
			
		}
		
		// add compartments for algos			
		Map<String,PaletteCompartmentEntry> categId2compartment = new HashMap<String, PaletteCompartmentEntry>();
		{
			
			for (String parentCategId : ExistingAlgoCategories.getExistingAlgoCategories().getParentCategories()) {
				AlgoCategory parentCateg = ExistingAlgoCategories.getExistingAlgoCategories().getCategoryForId(parentCategId);
				
				PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry(parentCateg.getName(), null);
				compartmentEntry.setInitiallyOpen(true);
				res.add(compartmentEntry);
				
				categId2compartment.put(parentCategId, compartmentEntry);
				
			}
			
		}
		
		// now fill these compartments
		{
			IFeatureProvider featureProvider = getFeatureProvider();
			ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
			for (ICreateFeature createFeature : createFeatures) {

				try {
	
					if (createFeature instanceof CreateIAlgoInstanceFeature) {
						
						IAlgo algo = ((CreateIAlgoInstanceFeature)createFeature).getAlgo();
						String id = ExistingAlgoCategories.getExistingAlgoCategories().getCategoryForId(
								algo.getCategoryId()
								).getTopParent().getId();
						
						PaletteCompartmentEntry compartmentEntry = categId2compartment.get(id);
						
						if (compartmentEntry == null) {
							GLLogger.errorTech("unable to find a compartement for algo id "+id+"; algo "+algo.getName()+" will not be displayed", getClass());
							continue;
						}
						
						ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(
								createFeature.getCreateName(), 
								createFeature.getCreateDescription(),
								createFeature.getCreateImageId(), 
								createFeature.getCreateLargeImageId(), 
								createFeature
								);
	
						compartmentEntry.addToolEntry(objectCreationToolEntry);
	
					}
				
				} catch (RuntimeException e) {
					GLLogger.errorTech(
							"error during the initialization of workflow tools; the tool "+createFeature.getCreateName()+" will not be available", 
							getClass(),
							e
							);
					
				}
			}
			
		}
		
		return res.toArray(new IPaletteCompartmentEntry[res.size()]);
	}

	/*
	
	TODO enhance ergonomy by implementing

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		return super.getContextMenu(context);
	}

	
	public String getToolTip(GraphicsAlgorithm ga) {
		return null;
	}
	
	public String getTitleToolTip() {
		return null;
	}
	
	
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		return null;
	}
	*/
	
}
