package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;

import java.io.File;

public class FileFlowType extends AbstractFlowType<File> {

	public static FileFlowType SINGLETON = new FileFlowType();

	protected FileFlowType() {
		super(
				"core.types.file",
				"file", 
				"a file stored into the filesystem"
				);
	}

	@Override
	public File decodeFrom(Object value) {
			
		if (value instanceof String)
			return new File((String)value);
		
		try {
			return (File)value;
		} catch (ClassCastException e) {
			throw new WrongParametersException("unable to cast File from "+value);
		}
	
	}


}
