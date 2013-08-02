package genlab.gui.graphiti;

import genlab.core.commons.FileUtils;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;

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
    public static final String SEEINFO_ID = PREFIX + "infos";
    
	public GraphitiImageProvider() {
	}


	public static String getImageIdForAlgo(IAlgo algo) {
		return algo.getId()+".graphiti.image";
	}
	
	protected boolean ensureFileExists(String algoName, String algoImg) {
		File f = new File(algoImg);
		if (!f.exists()) {
			GLLogger.warnTech("unable to reference the image for algo "+algoName+": file not found "+algoImg, getClass());
			return false;
		}
		if (!f.isFile()) {
			GLLogger.warnTech("unable to reference the image for algo "+algoName+": not a file "+algoImg, getClass());
			return false;
		}
		if (!f.canRead()) {
			GLLogger.warnTech("unable to reference the image for algo "+algoName+": no read permission "+algoImg, getClass());
			return false;
		}
		
		return true;
	}
	
	
	@Override
	protected void addAvailableImages() {
        
		// add "static" images (permanent)
		addImageFilePath(PARAMETERS_ID, "icons/gears.gif");
        addImageFilePath(SEEINFO_ID, "icons/question.gif");

        // add images for algos
        // nota: because the graphiti image provider is only looking for local files into its plugin directory,
        // we take the ugly solution of copying each image file into our directory structure.
        
        String algoImg = null;

        /*
        // 
    	File directory = null;
        {
	        URL url = Activator.getDefault().getBundle().getEntry("/");
	        URL resolvedURL = null;
			try {
				resolvedURL = FileLocator.toFileURL(url);
			} catch (IOException e) {
				GLLogger.errorTech("unable to locale the root of the plugin; icons will not be added for algorithms", getClass(), e);
				return;
			}
	        directory = new File(resolvedURL.getFile(), "iconsTmp");
	        if (directory.exists()) {
	        	if (directory.isDirectory()) { 
	        		// can use it :-)
	        	} else {
	        		GLLogger.errorTech("unable to create the directory for tmp icons: "+directory+"; there will be no icons in graphs", getClass());
	        		return;
	        	}
	        } else {
	        	directory.mkdirs();
	        }
        }
        */
        
        for (IAlgo algo : ExistingAlgos.getExistingAlgos().getAlgos()) {
        	algoImg = algo.getImagePath();
        	if (algoImg != null) {
        		GLLogger.traceTech("attempting to load image for algo "+algo.getName()+": "+algoImg, getClass());
        		
        		// ensure this image can be readen
        		/*
        		if (!ensureFileExists(algo.getName(),  algoImg)) {
        			GLLogger.warnTech("unable to find icon "+algoImg+"; this picture will not be available", getClass());
        			continue;
        		}
        		*/
        		
        		// open this image !
        		URL url = algo.getBundle().getEntry(
						algoImg
						);
        		if (url == null) {
        			GLLogger.warnTech("the bundle was unable to provide an URL for image: "+algoImg+"; this image will not be added", getClass());
        			continue;
        		}        			
        		addImageFilePath(
        				getImageIdForAlgo(algo), 
        				url.toString()
        				);
        		
        		/*
        		// 
        		File filedest = new File(directory, FileUtils.extractFilename(FileUtils.extractFilename(algoImg)));
        		boolean copied = FileUtils.copyFiles(
        				new File(algoImg), 
        				filedest
        				);
        		if (!copied) { 
        			GLLogger.errorTech("unable to copy icon from "+algoImg+" to "+filedest+"; this icon will not be available", getClass());
        			continue;
        		}
        		
        		String localPluginFile = "iconsTmp/"+FileUtils.extractFilename(algoImg);
        		GLLogger.debugTech("adding "+localPluginFile+" for "+getImageIdForAlgo(algo), getClass());

        		addImageFilePath(getImageIdForAlgo(algo), localPluginFile);
        		*/
        	}
        }
        
	}

}
