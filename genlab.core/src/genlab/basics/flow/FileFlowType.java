package genlab.basics.flow;

import java.io.File;

import genlab.core.algos.WrongParametersException;
import genlab.core.flow.IFlowType;

public class FileFlowType implements IFlowType<File> {

	@Override
	public String getShortName() {
		return "file";
	}

	@Override
	public String getDescription() {
		return "a file stored into the filesystem";
	}

	@Override
	public String getHtmlDescription() {
		return getDescription();
	}
	
	@Override
	public File decodeFrom(Object value) {
	
		// TODO accept String for filenmaes ? 
		
		try {
			return (File)value;
		} catch (ClassCastException e) {
			throw new WrongParametersException("unable to cast File from "+value);
		}
	
	}


}
