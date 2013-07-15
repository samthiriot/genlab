package genlab.gui.views;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * This view displays informations on a given algo
 * 
 * @author Samuel Thiriot
 */ 
public class AlgoInfoView extends ViewPart implements IPropertyChangeListener {

	public static final String PROPERTY_ALGO_ID = "algoId";
	
	private Browser browser;
		
	private IAlgo algo = null;
	
	private Map<Widget, Parameter<?>> widget2param = new HashMap<Widget, Parameter<?>>();
	
	public AlgoInfoView() {
		
		// listen for changes in our properties;
		// we will load data as soon as everything will be defined
		addPartPropertyListener(this);
		
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		showBusy(true);
		
		// if all properties are OK, then we load parameters
		
		try {
			// retrieve params
			final String algoId = this.getPartProperty(PROPERTY_ALGO_ID);
			if (algoId == null)
				return;
			
			// load corresponding algo
			algo = ExistingAlgos.getExistingAlgos().getAlgoForId(algoId);
			if (algo == null) {
				GLLogger.errorTech("unable to find the algo with id "+algoId+"; will not open the corresponding view", getClass());
				return;
			}
			
			GLLogger.debugTech("displaying info for algo instance "+algo.getId(), getClass());
			
			loadData();
		} finally {
			showBusy(false);
			
		}
	}
	
	protected void loadData() {
		
		if (browser == null || browser.isDisposed()) 
			return;
		
		final String desc = algo.getHTMLDescription();
		if (desc == null) {
			GLLogger.warnUser("no information provided for this algorithm, sorry", getClass());
			return;
		}
		
		if (desc.startsWith(BasicAlgo.markerFile)) {
			
			String path = desc.substring(BasicAlgo.markerFile.length()+1);
			GLLogger.debugTech("loading info from file: "+path, getClass());
			
			File test = (new File(path));
			if (!test.exists()) {
				path = "./"+path;
				test = (new File(path));
			}
			
			
			if (!test.exists()) {
				GLLogger.warnTech("unable to load the description: "+path, getClass());
					
			}
			
			browser.setUrl(test.getAbsolutePath());
		} else {
			GLLogger.debugTech("displaying html stored in Java", getClass());
			browser.setText(algo.getHTMLDescription());
		}
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.SIMPLE | SWT.READ_ONLY);
		} catch (RuntimeException e) {
			GLLogger.errorTech("unable to initialize a browser: "+e.getMessage(), getClass(), e);
		}
		
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void dispose() {
		browser.dispose();
		super.dispose();
	}



}
