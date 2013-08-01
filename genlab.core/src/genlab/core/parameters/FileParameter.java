package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class FileParameter extends Parameter<File> {

	public FileParameter(String id, String name, String desc, File defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@Override
	public Map<String, Boolean> check(File value) {
		Map<String,Boolean> res = new HashMap<String, Boolean>();
		
		if (!value.isFile())
			res.put("this file is not a file", true);
		if (!value.exists())
			res.put("this file does not exists", true);
		if (!value.canRead())
			res.put("this file should be readable", true);
		
		return res;
	}

	@Override
	public File parseFromString(String value) {

		File test = new File(value);
		
		Map<String, Boolean> errors = check(test); 
		if (!errors.isEmpty()) {
			// TODO error message
			throw new WrongParametersException("wrong value for a file");
		}
		return test;
	}

	
}
