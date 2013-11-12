package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.gui.graphiti.Clipboard;
import genlab.gui.graphiti.genlab2graphiti.WorkflowListener;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

public class CopyFeature extends AbstractFeature implements ICopyFeature {

	public CopyFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canCopy(ICopyContext context) {
		
		final PictogramElement[] pes = context.getPictogramElements();
        if (pes == null || pes.length == 0) {  // nothing selected
            return false;
        }
       
        // return true, if all selected elements are a EClasses
        for (PictogramElement pe : pes) {
            final Object bo = getBusinessObjectForPictogramElement(pe);
            
            // don't copy when there is a problem in the graphical representation
            // for instance if we are unable to find the object for a pictogram 
            if (bo == null)
            	return false;
            
            // 	copy only objects from genlab
            if (
            		!(bo instanceof IAlgoInstance)
            		&&
            		!(bo instanceof IConnection)
            		)
            	return false;
            
        }
        return true;
	}
	
	public void copy(ICopyContext context) {

		// get the business-objects for all pictogram-elements
        PictogramElement[] pes = context.getPictogramElements();
        Object[] bos = new Object[pes.length];
        for (int i = 0; i < pes.length; i++) {
        	final PictogramElement pe = pes[i];
        	Object bo = getBusinessObjectForPictogramElement(pe);
            bos[i] = bo;
            System.err.println("copying: "+bos[i]);
            
            // transmit the UI the infos that may be of use for paste
            WorkflowListener.lastInstance.transmitLastUIParameters(
    				bo,
    				new WorkflowListener.UIInfos() {{ 
    					x = pe.getGraphicsAlgorithm().getX();
    					y = pe.getGraphicsAlgorithm().getY();
    					width = pe.getGraphicsAlgorithm().getWidth();
    					height = pe.getGraphicsAlgorithm().getHeight();
    				}}
    		);
            
        }
        
        // put all business objects to the clipboard
        Clipboard.singleton.setObjects(bos);

	}
	

	@Override
	public boolean canExecute(IContext context) {
		boolean ret = false;
		if (context instanceof ICopyContext) {
			ret = canCopy((ICopyContext) context);
		}
		return ret;
	}

	@Override
	public void execute(IContext context) {
		if (context instanceof ICopyContext) {
			copy((ICopyContext) context);
		}
	}




}
