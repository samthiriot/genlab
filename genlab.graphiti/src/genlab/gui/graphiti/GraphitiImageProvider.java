package genlab.gui.graphiti;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

/**
 * Provides images for our use of graphiti.
 * 
 * Registered through an extension point.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphitiImageProvider extends AbstractImageProvider {

    protected static final String PREFIX = "genlab.gui.graphiti.images.";
    
    public static final String PARAMETERS_ID = PREFIX + "parameters";
    
	public GraphitiImageProvider() {
	}

	@Override
	protected void addAvailableImages() {
        addImageFilePath(PARAMETERS_ID, "icons/gears.gif");

	}

}
