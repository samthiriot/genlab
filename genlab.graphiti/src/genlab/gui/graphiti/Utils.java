package genlab.gui.graphiti;

import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;


public class Utils {

	/**
	 * Returns the top container, supposed to be the graphic
	 * @param anchor
	 * @return
	 */
	public static ContainerShape getTopContainer(Anchor anchor) {
		
		/*
		Object previousParent = null;
		Object candidateParent = null;
		*/
		//candidateParent = 
				
		EObject o = anchor.eContainer();
		EObject previousO = o;
		// attempt to get at the very top 
		while (o != null) {
			previousO = o;
			o = o.eContainer();
		}
		// previousO now contains the top top
		return (ContainerShape)previousO;
		
		
		
	}
	
	private Utils() {
		
	}

}
