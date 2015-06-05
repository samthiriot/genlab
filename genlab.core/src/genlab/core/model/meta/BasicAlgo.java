package genlab.core.model.meta;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.BooleanParameter;
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

	protected final String id;
	protected final String categoryId;
	protected final Set<IInputOutput> outputs = new LinkedHashSet<IInputOutput>();
	protected final Set<IInputOutput> inputs = new LinkedHashSet<IInputOutput>();
	protected final Map<String,Parameter<?>> parameters = new LinkedHashMap<String,Parameter<?>>();
	
	protected String absoluteImagePath = null;
	
	
	protected String imageRelativePath = null;
	protected String imageRelativeBig = null;

	public static final BooleanParameter PARAM_DISABLED = new BooleanParameter(
			"param_disabled", 
			"disabled", 
			"don't run this algorithm even if it is in the workflow", 
			Boolean.FALSE
			);
	
	public static final String IMAGE_PATH_PLACEHOLDER_SIZE = "[%SIZE%]";
	
	/**
	 * Pass the image parameter with a placeholder for size: 
	 * @param name
	 * @param description
	 * @param longHtmlDescription
	 * @param categoryId
	 * @param imagePath
	 */
	public BasicAlgo(
			String name,
			String description,
			AlgoCategory category,
			String imagePath,
			String imagePathBig
			) {
		
		this.id = constructId(name);
		this.name = name;
		this.description = description;

		this.categoryId = category.getTotalId();
		
		this.imageRelativePath = imagePath;
		this.imageRelativeBig = imagePathBig;
		
		registerParameter(PARAM_DISABLED);
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
		
		registerParameter(PARAM_DISABLED);

	}

	/**
	 * Should be overriden and implemented if an image is provided
	 * @return
	 */
	@Override
	public Bundle getBundle() {
		throw new NotImplementedException("algorithms which provide images should override this method");
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
	public String getImagePath16X16() {
		
		return (imageRelativePath == null? null : imageRelativePath.replace(IMAGE_PATH_PLACEHOLDER_SIZE, "16x16"));
		/*
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
		*/
	}
	

	@Override
	public String getImagePath32X32() {
		return (imageRelativePath == null? null : imageRelativePath.replace(IMAGE_PATH_PLACEHOLDER_SIZE, "32x32"));	
	}

	@Override
	public String getImagePath64X64() {
		return (imageRelativePath == null? null : imageRelativePath.replace(IMAGE_PATH_PLACEHOLDER_SIZE, "64x64"));
	}
	
	@Override
	public String getImagePathBig() {
		return imageRelativeBig;
	}

	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		// by default, deducts that from generic type
		return canBeContainedInto((IAlgoContainer)algoInstance.getAlgo());
	}

	@Override
	public boolean canBeContainedInto(IAlgoContainer algoContainer) {
		// by default, returns true
		return true;
	}
	

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// by default, will return a sum of inputs and outputs
		return -1 - inputs.size() - outputs.size();
	}

	@Override
	public IInputOutput<?> getInputInstanceForId(String inputId) {
		for (IInputOutput<?> io: inputs) {
			if (io.getId().equals(inputId))
				return io;
		}
		return null;
	}

	@Override
	public IInputOutput<?> getOutputInstanceForId(String outputId) {
		for (IInputOutput<?> io: outputs) {
			if (io.getId().equals(outputId))
				return io;
		}
		return null;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}



}
