package genlab.core.model.meta.basics.algos;

import genlab.core.commons.FileUtils;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.parameters.FileParameter;
import genlab.core.parameters.Parameter;

import java.io.File;

public class ConstantValueFile extends ConstantValue<File> {

	public static final InputOutput<File> OUTPUT = new InputOutput<File>(
			FileFlowType.SINGLETON, 
			"constantvalue.double.file", 
			"constant output", 
			"an output which is constant"
			);
	
	public ConstantValueFile() {
		super(
				FileFlowType.SINGLETON, 
				OUTPUT, 
				"constant file", 
				"a constant file value",
				null				
				);
		
	}

	@Override
	protected Parameter<File> createConstantParameter() {
		return new FileParameter(paramId, "value", "the value of this constant", FileUtils.getHomeDirectoryFile());
	}

	


}
