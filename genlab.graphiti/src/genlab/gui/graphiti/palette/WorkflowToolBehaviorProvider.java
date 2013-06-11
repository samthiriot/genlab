package genlab.gui.graphiti.palette;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.features.CreateIAlgoInstanceFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.internal.Messages;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public class WorkflowToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public WorkflowToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

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
			}
			
		}
		
		return res.toArray(new IPaletteCompartmentEntry[res.size()]);
	}

	
}
