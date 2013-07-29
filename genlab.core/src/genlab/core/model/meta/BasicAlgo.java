package genlab.core.model.meta;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

public abstract class BasicAlgo implements IAlgo {

	protected final String name;
	protected final String description;
	protected final String longHtmlDescription;

	protected final String id;
	protected final String categoryId;
	protected final Set<IInputOutput> outputs = new LinkedHashSet<IInputOutput>();
	protected final Set<IInputOutput> inputs = new LinkedHashSet<IInputOutput>();
	protected final Map<String,Parameter<?>> parameters = new LinkedHashMap<String,Parameter<?>>();
	
	protected String absoluteImagePath = null;
	
	
	protected String imageRelativePath = null;
	
	public BasicAlgo(
			String name,
			String description,
			String longHtmlDescription,
			String categoryId,
			String imagePath
			) {
		
		this.id = constructId(name);
		this.name = name;
		this.description = description;

		this.longHtmlDescription = longHtmlDescription;
		if (longHtmlDescription == null)
			GLLogger.warnTech("this algo has no detailed description: "+id, getClass());
		this.categoryId = categoryId;
		
		this.imageRelativePath = imagePath;
	}
	
	protected String constructId(String name) {
		
		// return name.replaceAll("[-+.^:, ]","_");
		return getClass().getCanonicalName();
	}

	public BasicAlgo(
			String name,
			String description,
			String categoryId
			) {
		
		this(name, description, null, categoryId, null);
	}

	/**
	 * Should be overriden and implemented if an image is provided
	 * @return
	 */
	public Bundle getBundle() {
		throw new NotImplementedException();
	}
	
	
	
	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	@Override
	public final Set<IInputOutput> getInputs() {
		return Collections.unmodifiableSet(inputs);
	}

	@Override
	public final Set<IInputOutput> getOuputs() {
		return Collections.unmodifiableSet(outputs);
	}

	@Override
	public final String getId() {
		return id;
	}
	
	public final String getCategoryId() {
		return categoryId;
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new AlgoInstance(this, workflow);
	}
	
	@Override
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new AlgoInstance(this, workflow, id);
	}

	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Collection<Parameter<?>> getParameters() {
		return parameters.values();
	}
	

	@Override
	public Parameter<?> getParameter(String id) {
		return parameters.get(id);
	}

	@Override
	public boolean hasParameter(String id) {
		return parameters.containsKey(id);
	}
	
	protected void registerParameter(Parameter<?> p) {
		this.parameters.put(p.getId(), p);
	}

	@Override
	public String getHTMLDescription() {
		return longHtmlDescription;
	}
	
	public final static String markerFile = "___fromFile/";
	
	protected static String loadHtmlDescription(String pathFile) {
		
		return markerFile+pathFile;
		
	}

	protected static String buildHtmlDescription(
			String name,
			String shortDescription,
			String plugin,
			String authorsGenlab,
			String pluginOriginal,
			String authorsOriginal,
			String description
			) {
		StringBuffer sb = new StringBuffer();
		
		return sb.toString();
	}
	
	@Override
	public String getImagePath() {
		
		if (absoluteImagePath == null && imageRelativePath != null) {
			File file = null;
			try {
				// Resolve the URL
				URL basicUrl = getBundle().getEntry(imageRelativePath);
				URL resolvedURL = FileLocator.toFileURL(basicUrl);
				file = new File (resolvedURL.getFile ());
				absoluteImagePath = file.getAbsolutePath();
			} catch (Exception e) {
				// Something sensible if an error occurs
				GLLogger.errorTech("unable to resolve file : "+imageRelativePath+"; the image will not be available for this algo", getClass());
				absoluteImagePath = null;
				
			}
		}
		
		
		return absoluteImagePath;
	}

}
