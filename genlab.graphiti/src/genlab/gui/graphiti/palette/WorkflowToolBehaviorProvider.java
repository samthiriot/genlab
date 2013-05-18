package genlab.gui.graphiti.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
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
			for (int i = 0; i < superCompartments.length; i++)
				res.add(superCompartments[i]);
		}
		
		// add a compartment for algos
		{
			PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Algos", null);
			res.add(compartmentEntry);
			/*
			ICreateFeature[] createFeatures =  .getCreateFeatures();
			    for (ICreateFeature cf : createFeatures) {
			        ObjectCreationToolEntry objectCreationToolEntry = 
			               new ObjectCreationToolEntry(cf.getCreateName(),
			                 cf.getCreateDescription(), cf.getCreateImageId(),
			                    cf.getCreateLargeImageId(), cf);
			        stackEntry.addCreationToolEntry(objectCreationToolEntry);
			    }
			*/
		}
		return res.toArray(new IPaletteCompartmentEntry[res.size()]);
	}

	
}
