package genlab.gui.graphiti.genlab2graphiti;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.notification.INotificationService;

/**
 * DOubtful utility now
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabNotificationService implements INotificationService {

	private IDiagramTypeProvider diagramTypeProvider;

	public GenlabNotificationService(IDiagramTypeProvider diagramTypeProvider) {
		
		this.diagramTypeProvider = diagramTypeProvider;

	}
	

	/**
	 * Calculate linked pictogram elements.
	 * 
	 * @param changedAndRelatedBOsList
	 *            the changed and related BOs list
	 * @return the pictogram element[]
	 */
	protected PictogramElement[] calculateLinkedPictogramElements(ArrayList<Object> changedAndRelatedBOsList) {
		
		System.err.println("calculateLinkedPictogramElements "+changedAndRelatedBOsList.toString());

		ArrayList<PictogramElement> retList = new ArrayList<PictogramElement>();
		final IFeatureProvider featureProvider = diagramTypeProvider.getFeatureProvider();
		for (Object crbo : changedAndRelatedBOsList) {
			final PictogramElement[] allPictogramElementsForBusinessObject = featureProvider.getAllPictogramElementsForBusinessObject(crbo);
			for (PictogramElement pe : allPictogramElementsForBusinessObject) {
				retList.add(pe);
			}
		}
		return retList.toArray(new PictogramElement[0]);
	}
	
	/**
	 * Calculate dirty pictogram elements.
	 * 
	 * @param changedBOs
	 *            the changed business objects
	 * @return the pictogram element[]
	 */
	public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
		
		System.err.println("calculateRelatedPictogramElements "+Arrays.toString(changedBOs));
		
		ArrayList<Object> changedAndRelatedBOsList = new ArrayList<Object>();
		for (Object cbo : changedBOs) {
			changedAndRelatedBOsList.add(cbo);
		}
		Object[] relatedBOs = diagramTypeProvider.getRelatedBusinessObjects(changedBOs);
		for (Object rbo : relatedBOs) {
			changedAndRelatedBOsList.add(rbo);
		}

		return calculateLinkedPictogramElements(changedAndRelatedBOsList);
	}

	@Override
	public void updatePictogramElements(PictogramElement[] dirtyPes) {
		
		System.err.println("updatePictogramElements "+Arrays.toString(dirtyPes));
		
		final IDiagramTypeProvider dtp = diagramTypeProvider;
		final IFeatureProvider fp = dtp.getFeatureProvider();
		for (PictogramElement pe : dirtyPes) {
			final UpdateContext updateContext = new UpdateContext(pe);
			// fp.updateIfPossible(updateContext);
			fp.updateIfPossibleAndNeeded(updateContext);
		}
	}

}
